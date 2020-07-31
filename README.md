# OBO taxon constraints plugin for Protégé

OBO ontologies such as Gene Ontology, Uberon anatomy ontology, and the Cell Ontology include axioms specifying the organism taxon in which instances of particular concepts are found. The OBO Relation Ontology provides axioms that propagate these taxon constraints over other relations between concepts such as 'part of' and 'develops from'. These taxon constraint axioms provide a powerful means to ensure consistent and taxon-appropriate application of ontology terms, but it can be hard to know which taxon constraints apply to a term, when the chain of reasoning can extend across many concepts. This plugin use the active reasoner within Protégé to compute the taxon constraints in effect for any selected term.

## Installation

*Forthcoming*

## Usage

Once you have the plugin installed, you can add it to your Protégé window by going to the menu `Window > Views > OBO views > Taxon constraints`, and the clicking the location to place the panel. The plugin will show the taxon constraints in effect for the selected OWL class. When a reasoner is running, any inferred taxon constraints will be shown along with directly asserted ones.

## Building

To ensure compatibility with Protégé, you should be using Java 8. Building the plugin requires having `sbt` installed on your system. Inside the project folder, run `sbt osgiBundle` to build the plugin jar, which will be created in `./target/taxon-constraints-<version>.jar`.
