package org.geneontology.taxonconstraints

import java.util

import org.protege.editor.owl.OWLEditorKit
import org.protege.editor.owl.ui.editor.OWLObjectEditor
import org.protege.editor.owl.ui.frame.{AbstractOWLFrameSectionRow, OWLFrameSection}
import org.semanticweb.owlapi.model.{OWLClass, OWLEquivalentClassesAxiom, OWLOntology}

import scala.jdk.CollectionConverters._

case class NeverInTaxonFrameSectionRow(
                                        owlEditorKit: OWLEditorKit, section: OWLFrameSection[OWLClass, OWLEquivalentClassesAxiom, OWLClass],
                                        ontology: OWLOntology,
                                        rootObject: OWLClass,
                                        equivAxiom: OWLEquivalentClassesAxiom,
                                        inferred: Boolean) extends AbstractOWLFrameSectionRow[OWLClass, OWLEquivalentClassesAxiom, OWLClass](
  owlEditorKit, section, ontology, rootObject, equivAxiom) {

  override def getObjectEditor: OWLObjectEditor[OWLClass] = null

  override def createAxiom(editedObject: OWLClass): OWLEquivalentClassesAxiom = equivAxiom

  override def getManipulatableObjects: util.List[OWLClass] = List(rootObject).asJava

  override def isEditable: Boolean = false

  override def isDeleteable: Boolean = false

  override def isInferred: Boolean = inferred

}
