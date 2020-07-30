package org.geneontology.taxonconstraints

import org.semanticweb.owlapi.apibinding.OWLManager
import org.semanticweb.owlapi.model._
import org.semanticweb.owlapi.reasoner.OWLReasoner

import scala.jdk.CollectionConverters._

object InferredTaxonConstraints {

  private val factory = OWLManager.getOWLDataFactory
  val InTaxon: OWLObjectProperty = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002162"))
  val OnlyInTaxon: OWLObjectProperty = factory.getOWLObjectProperty(IRI.create("http://purl.obolibrary.org/obo/RO_0002160"))
  val Root: OWLClass = factory.getOWLClass(IRI.create("http://purl.obolibrary.org/obo/NCBITaxon_1"))
  val taxonConstraintProperties: Set[OWLObjectPropertyExpression] = Set(InferredTaxonConstraints.OnlyInTaxon, InferredTaxonConstraints.InTaxon)

  def alwaysAxiom(term: OWLClass, filler: OWLClass): OWLSubClassOfAxiom =
    factory.getOWLSubClassOfAxiom(term, factory.getOWLObjectSomeValuesFrom(InTaxon, filler))

  def neverAxiom(term: OWLClass, filler: OWLClass): OWLEquivalentClassesAxiom =
    factory.getOWLEquivalentClassesAxiom(factory.getOWLObjectIntersectionOf(term, factory.getOWLObjectSomeValuesFrom(InTaxon, filler)), factory.getOWLNothing)

  def findAlwaysFillers(term: OWLClass, filler: OWLClass, reasoner: OWLReasoner, seen: Set[OWLClass] = Set.empty): Set[OWLClass] = {
    val er = factory.getOWLObjectSomeValuesFrom(InTaxon, filler)
    val conj = factory.getOWLObjectIntersectionOf(term, er)
    val isEntailed = reasoner.getEquivalentClasses(conj).contains(term)
    val updatedSeen = seen + filler
    if (isEntailed) {
      val moreSpecificFillers = reasoner.getSubClasses(filler, true).getFlattened.asScala.toSet
        .filterNot(_.isOWLNothing)
        .filterNot(updatedSeen)
        .flatMap(subclass => findAlwaysFillers(term, subclass, reasoner, updatedSeen))
      if (moreSpecificFillers.isEmpty) Set(filler) else moreSpecificFillers
    } else Set.empty
  }

  private def computeNeverFillers(term: OWLClass, filler: OWLClass, reasoner: OWLReasoner, seen: Set[OWLClass]): Set[OWLClass] = {
    val er = factory.getOWLObjectSomeValuesFrom(InTaxon, filler)
    val conj = factory.getOWLObjectIntersectionOf(term, er)
    val satisfiable = reasoner.isSatisfiable(conj)
    val updatedSeen = seen + filler
    if (satisfiable) reasoner.getSubClasses(filler, true).getFlattened.asScala.toSet
      .filterNot(_.isOWLNothing)
      .filterNot(updatedSeen)
      .flatMap(subclass => computeNeverFillers(term, subclass, reasoner, updatedSeen))
    else Set(filler)
  }

  def findNeverFillers(term: OWLClass, filler: OWLClass, reasoner: OWLReasoner): Set[OWLClass] = {
    val fillers = computeNeverFillers(term, filler, reasoner, Set.empty)
    fillers.filter { term =>
      val superclasses = reasoner.getSuperClasses(term, false).getFlattened.asScala.toSet
      fillers.intersect(superclasses).isEmpty
    }
  }

}
