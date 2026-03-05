# MedHead - Système d'Intervention d'Urgence (PoC)

Ce projet PoC est développée pour le consortium **MedHead**. Son but est de valider l'architecture cible pour l'allocation en temps réel de lits d'hôpitaux au sein du système de santé britannique (NHS).

## Objectifs de la PoC
Le succès de la mise en œuvre est déterminé par les indicateurs suivants :
* **Acheminement** : Plus de 90 % des cas d'urgence sont acheminés vers l'hôpital compétent le plus proche du réseau.
* **Performance** : Le temps moyen de traitement d'une urgence passe de 18,25 minutes à 12 minutes.
* **Réactivité Technique** : Temps de réponse de moins de 200 millisecondes avec une charge de 800 requêtes par seconde.

## Workflow Git
Le comité d'architecture impose un workflow détaillé pour être réutilisable par les équipes. Nous utiliserons le **GitHub Flow** pour répondre au principe de livraison continue.

1.  **Branche `main`** : Branche principale contenant le code stable et validé.
2.  **Branches de fonctionnalités (`feature-`)** : Utilisées pour l'intégration de petits changements incrémentiels afin de réduire les risques.
3.  **Traçabilité** : Les fonctionnalités sont clairement traçables dans le contrôle de version via un étiquetage approprié.
4.  **Validation** : Les exécutions CI/CD sont liées à une livraison de fonctionnalité donnée pour isoler les erreurs.

## Installation et Exécution
### Prérequis
* **Technologie Java** : Imposée comme socle technique fiable par le consortium.
* **JDK 21 (LTS)** : Choisi pour garantir la continuité des systèmes critiques.
* **Maven** : Utilisé pour la gestion des builds et des dépendances selon les standards du projet.

### Lancement
1.  **Build** : `./mvnw clean install`
2.  **Exécution** : `./mvnw spring-boot:run`

## Stratégie de Tests
Le projet respecte la pyramide des tests (unitaires, intégration et E2E) pour garantir la fiabilité.

* **Tests Unitaires & Intégration** : Validation de la logique de calcul de distance et d'allocation de lits.
* **Tests de Stress** : Utilisation d'outils (type JMeter) pour garantir la continuité de l'activité en cas de pic d'utilisation.
* **BDD (Behavior Driven Development)** : Les plans de test utilisent des scénarios BDD pour décrire les critères d'acceptation métier.

## Pipeline CI/CD
L'intégration et la livraison continues sont automatisées pour réduire les risques d'intégration :
1.  **Déclenchement** : Automatique par des événements liés à l'état du code poussé.
2.  **Rapports** : Production de rapports d'exécution pour documenter les comportements livrés.
3.  **Transparence** : Les journaux permettent d'isoler les builds en échec ou les erreurs d'étape.

## Protection des données et Conformité
* **RGPD** : Les données réelles des patients doivent à tout moment rester conformes aux réglementations européennes.
* **Anonymisation** : Utilisation de données factices ou anonymisées pour les activités de la PoC.
* **Sécurité Shift-Left** : Les exigences de sécurité sont respectées dès le début de chaque incrément.
