package org.geneontology.taxonconstraints

import java.util.Comparator

import org.protege.editor.owl.OWLEditorKit
import org.protege.editor.owl.model.OWLEditorKitShortFormProvider
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask
import org.protege.editor.owl.ui.editor.OWLObjectEditor
import org.protege.editor.owl.ui.frame.{AbstractOWLFrameSection, OWLFrame, OWLFrameSectionRow}
import org.semanticweb.owlapi.model.{OWLClass, OWLObjectSomeValuesFrom, OWLOntology, OWLSubClassOfAxiom}
import org.semanticweb.owlapi.search.EntitySearcher
import org.semanticweb.owlapi.util.OWLEntityComparator

import scala.jdk.CollectionConverters._

class AlwaysInTaxonFrameSection(editorKit: OWLEditorKit, frame: OWLFrame[OWLClass]) extends
  AbstractOWLFrameSection[OWLClass, OWLSubClassOfAxiom, OWLClass](editorKit, "Always in taxon", frame) {

  private lazy val classComparator = new OWLEntityComparator(new OWLEditorKitShortFormProvider(this.getOWLEditorKit))

  override def createAxiom(cls: OWLClass): OWLSubClassOfAxiom = null

  override def getObjectEditor: OWLObjectEditor[OWLClass] = null

  private def assertedAlwaysInTaxa(term: OWLClass, ontologies: Set[OWLOntology]): Set[OWLClass] = {
    EntitySearcher.getSuperClasses(term, ontologies.asJava).asScala.to(Set).collect {
      case superClass: OWLObjectSomeValuesFrom
        if InferredTaxonConstraints.taxonConstraintProperties(superClass.getProperty) && superClass.getFiller.isNamed =>
        superClass.getFiller.asOWLClass
    }
  }

  override def refill(ontology: OWLOntology): Unit = {
    val term = getRootObject
    val rows = assertedAlwaysInTaxa(term, Set(ontology)).map(filler => new AlwaysInTaxonFrameSectionRow(this.editorKit, this, ontology, filler, InferredTaxonConstraints.alwaysAxiom(term, filler), false))
    rows.foreach(addRow)
  }

  override def refillInferred(): Unit = {
    val term = getRootObject
    val asserted = assertedAlwaysInTaxa(term, getReasoner.getRootOntology.getImportsClosure.asScala.to(Set))
    getOWLModelManager.getReasonerPreferences.executeTask(OptionalInferenceTask.SHOW_INFERRED_SUPER_CLASSES, () => {
      val mostSpecificFillers = InferredTaxonConstraints.findAlwaysFillers(term, InferredTaxonConstraints.Root, getReasoner)
        .filterNot(asserted)
        .map(filler => new AlwaysInTaxonFrameSectionRow(this.editorKit, this, this.getOWLModelManager.getActiveOntology, filler, InferredTaxonConstraints.alwaysAxiom(term, filler), true))
      mostSpecificFillers.foreach(addRow)
    }
    )
  }

  override def clear(): Unit = () //TODO maybe need to dispose rows

  override def getRowComparator: Comparator[OWLFrameSectionRow[OWLClass, OWLSubClassOfAxiom, OWLClass]] =
    (left: OWLFrameSectionRow[OWLClass, OWLSubClassOfAxiom, OWLClass], right: OWLFrameSectionRow[OWLClass, OWLSubClassOfAxiom, OWLClass]) => classComparator.compare(left.getRoot, right.getRoot)

  override def canAdd: Boolean = false

}
