package org.geneontology.taxonconstraints

import java.util

import org.protege.editor.owl.OWLEditorKit
import org.protege.editor.owl.ui.editor.OWLObjectEditor
import org.protege.editor.owl.ui.frame.{AbstractOWLFrameSectionRow, OWLFrameSection}
import org.semanticweb.owlapi.model.{OWLClass, OWLOntology, OWLSubClassOfAxiom}

import scala.jdk.CollectionConverters._

class AlwaysInTaxonFrameSectionRow(
                                    owlEditorKit: OWLEditorKit, section: OWLFrameSection[OWLClass, OWLSubClassOfAxiom, OWLClass],
                                    ontology: OWLOntology,
                                    rootObject: OWLClass,
                                    subAxiom: OWLSubClassOfAxiom,
                                    inferred: Boolean) extends AbstractOWLFrameSectionRow[OWLClass, OWLSubClassOfAxiom, OWLClass](
  owlEditorKit, section, ontology, rootObject, subAxiom) {

  override def getObjectEditor: OWLObjectEditor[OWLClass] = null

  override def createAxiom(editedObject: OWLClass): OWLSubClassOfAxiom = subAxiom

  override def getManipulatableObjects: util.List[OWLClass] = List(rootObject).asJava

  override def isEditable: Boolean = false

  override def isDeleteable: Boolean = false

  override def isInferred: Boolean = inferred

}
