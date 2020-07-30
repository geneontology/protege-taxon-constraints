package org.geneontology.taxonconstraints

import java.awt.BorderLayout

import javax.swing.JScrollPane
import org.protege.editor.owl.ui.framelist.{OWLFrameList, OWLFrameListRenderer}
import org.protege.editor.owl.ui.view.cls.AbstractOWLClassViewComponent
import org.semanticweb.owlapi.model.OWLClass

class TaxonConstraintsViewComponent extends AbstractOWLClassViewComponent {

  private var frameList: OWLFrameList[OWLClass] = _

  override def initialiseClassView(): Unit = {
    val frame = new InTaxonFrame(this.getOWLEditorKit)
    frameList = new OWLFrameList[OWLClass](this.getOWLEditorKit, frame)
    frameList.setCellRenderer(new OWLFrameListRenderer(this.getOWLEditorKit))
    setLayout(new BorderLayout(10, 10))
    this.add(new JScrollPane(frameList))
    this.validate()
  }

  override def updateView(selectedClass: OWLClass): OWLClass = {
    this.frameList.setRootObject(selectedClass)
    selectedClass
  }

  override def disposeView(): Unit = this.frameList.dispose()

}
