# MedHead - Emergency Responder PoC

Preuve de Concept (PoC) pour le système d'affectation des lits d'urgence MedHead, permettant de trouver l'hôpital le plus proche disposant de lits disponibles pour une spécialité donnée.

## Prérequis
- **Java 21**
- **Node.js 20** (et npm)
- **Git**

---

## Installation

1. Cloner le dépôt sur votre machine locale :
   ```bash git clone https://github.com/cpichaud/medhead-emergency-responder-camille-pichaud.git

2. Cloner le dépôt d'architecture, contenant le document de Reporting et les livrables TOGAF :
   ```bash git clone 

## Back-end (Spring Boot / Java)

Le back-end expose une API REST pour traiter la recherche d'hôpitaux et publie des événements asynchrones lors des réservations.

### Builder
Ouvrez un terminal dans le dossier `emergency-responder` :
1. Télécharger les dépendances et compiler : `./mvnw clean install -DskipTests`
2. Lancer l'application : `./mvnw spring-boot:run`
Le serveur démarrera sur `http://localhost:8080`.

### Exécuter les tests
Les tests incluent la validation unitaire de logique métier (Mockito) et l'intégration des contrôleurs.
- Lancer les tests : `./mvnw clean test`

Les tests de stress de l'API ont également été réalisés via JMeter, le rapport est disponible dans la documentation d'architecture

---

## Front-end (VueJS)

L'interface utilisateur permet de sélectionner une spécialité médicale et d'obtenir le nom de l'établissement assigné.

### Builder
Ouvrez un terminal dans le dossier `medhead-front` :
1. Installer les dépendances : `npm install`
2. Lancer le serveur de développement : `npm run dev`
L'interface sera accessible sur `http://localhost:5173`.

### Exécuter les tests
Les tests End-to-End (E2E) sont réalisés avec Cypress pour valider le comportement de l'interface.
1. Démarrer le front-end : `npm run dev`
2. Dans un second terminal, lancer Cypress : `npx cypress run` (ou `npx cypress open` pour l'interface graphique).

---

## Pipeline CI/CD

L'intégration continue est entièrement automatisée via GitHub Actions. Le pipeline garantit que le code est sain à chaque modification.

**Fonctionnement de la chaîne de build :**
À chaque `push` ou `pull request` sur la branche principale, le workflow `.github/workflows/ci-cd.yml` se déclenche et exécute deux jobs en parallèle sur des machines virtuelles :
1. **Job Back-end :** Installe le JDK 21, télécharge les dépendances Maven, compile le code Java et exécute les tests JUnit.
2. **Job Front-end :** Installe Node.js 20, installe les dépendances npm, lance le serveur Vite en arrière-plan et exécute les tests E2E Cypress.
*Si l'un des tests échoue, le pipeline est bloqué et la fusion est empêchée.*

---

## Règles de versionning

Ce projet suit le **Feature Branch Workflow** (ou GitHub Flow) pour garantir la stabilité de l'application :
1. **Branche `main` :** C'est la branche de référence. Elle doit toujours être stable, fonctionnelle et passer tous les tests de la CI. On ne pousse **jamais** de code directement sur `main`.
2. **Branches de travail (`feature-` ou `fix-`) :** Pour chaque nouvelle fonctionnalité ou correction, une branche isolée est créée depuis `main` (ex: `feature-ci-cd`).
3. **Pull Request :** Une fois le développement terminé sur la branche, une PR est ouverte vers `main`.
4. **Validation :** L'ouverture de la PR déclenche les tests automatisés (GitHub Actions).
5. **Merge :** Le code n'est fusionné dans `main` que si le pipeline CI est au vert (succès total des tests et de la compilation).

