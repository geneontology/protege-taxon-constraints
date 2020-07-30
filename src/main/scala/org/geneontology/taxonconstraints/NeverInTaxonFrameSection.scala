package org.geneontology.taxonconstraints

import java.util.Comparator

import org.protege.editor.owl.OWLEditorKit
import org.protege.editor.owl.model.OWLEditorKitShortFormProvider
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask
import org.protege.editor.owl.ui.editor.OWLObjectEditor
import org.protege.editor.owl.ui.frame.{AbstractOWLFrameSection, OWLFrame, OWLFrameSectionRow}
import org.semanticweb.owlapi.model.{OWLClass, OWLEquivalentClassesAxiom, OWLObjectSomeValuesFrom, OWLOntology}
import org.semanticweb.owlapi.search.EntitySearcher
import org.semanticweb.owlapi.util.OWLEntityComparator

import scala.jdk.CollectionConverters._

class NeverInTaxonFrameSection(editorKit: OWLEditorKit, frame: OWLFrame[OWLClass]) extends
  AbstractOWLFrameSection[OWLClass, OWLEquivalentClassesAxiom, OWLClass](editorKit, "Never in taxon", frame) {

  private lazy val classComparator = new OWLEntityComparator(new OWLEditorKitShortFormProvider(this.getOWLEditorKit))

  override def createAxiom(cls: OWLClass): OWLEquivalentClassesAxiom = null

  override def getObjectEditor: OWLObjectEditor[OWLClass] = null

  private def assertedNeverInTaxa(term: OWLClass, ontologies: Set[OWLOntology]): Set[OWLClass] = {
    EntitySearcher.getDisjointClasses(term, ontologies.asJava).asScala.to(Set).collect {
      case other: OWLObjectSomeValuesFrom
        if InferredTaxonConstraints.taxonConstraintProperties(other.getProperty) && other.getFiller.isNamed =>
        other.getFiller.asOWLClass
    }
  }

  override def refill(ontology: OWLOntology): Unit = {
    val term = getRootObject
    println(s"Refill never for $term")
    println(s"Asserted never: ${assertedNeverInTaxa(term, Set(ontology))}")
    val rows = assertedNeverInTaxa(term, Set(ontology)).map(filler => new NeverInTaxonFrameSectionRow(this.editorKit, this, ontology, filler, InferredTaxonConstraints.neverAxiom(term, filler), false))
    rows.foreach(addRow)
  }

  override def refillInferred(): Unit = {
    val term: OWLClass = getRootObject
    val asserted = assertedNeverInTaxa(term, getReasoner.getRootOntology.getImportsClosure.asScala.to(Set))
    println(s"Asserted never: $asserted")
    getOWLModelManager.getReasonerPreferences.executeTask(OptionalInferenceTask.SHOW_INFERRED_SUPER_CLASSES, () => {
      val mostSpecificFillers = InferredTaxonConstraints.findNeverFillers(term, InferredTaxonConstraints.Root, getReasoner)
        .filterNot(asserted)
        .map(filler => new NeverInTaxonFrameSectionRow(this.editorKit, this, this.getOWLModelManager.getActiveOntology, filler, InferredTaxonConstraints.neverAxiom(term, filler), true))
      println(s"Adding ${mostSpecificFillers.size} inferred never rows")
      mostSpecificFillers.foreach(addRow)
    }
    )
  }

  override def clear(): Unit = () //TODO maybe need to dispose rows

  override def getRowComparator: Comparator[OWLFrameSectionRow[OWLClass, OWLEquivalentClassesAxiom, OWLClass]] =
    (left: OWLFrameSectionRow[OWLClass, OWLEquivalentClassesAxiom, OWLClass], right: OWLFrameSectionRow[OWLClass, OWLEquivalentClassesAxiom, OWLClass]) => classComparator.compare(left.getRoot, right.getRoot)

  override def canAdd: Boolean = false

}
