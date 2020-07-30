package org.geneontology.taxonconstraints

import org.protege.editor.owl.OWLEditorKit
import org.protege.editor.owl.ui.frame.AbstractOWLFrame
import org.semanticweb.owlapi.model.OWLClass

class InTaxonFrame(editorKit: OWLEditorKit) extends AbstractOWLFrame[OWLClass](editorKit.getOWLModelManager.getOWLOntologyManager) {

  addSection(new AlwaysInTaxonFrameSection(editorKit, this))
  addSection(new NeverInTaxonFrameSection(editorKit, this))

}
