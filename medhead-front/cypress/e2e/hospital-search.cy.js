describe("Validation complète de l'interface MedHead", () => {

  beforeEach(() => {
    // Interception par défaut pour les succès
    cy.intercept('GET', '**/api/hospitals/nearest*', (req) => {
      if (req.url.includes('specialism=Cardiologie')) {
        req.reply({
          statusCode: 200,
          body: { id: 1, name: "London Central Emergency", availableBeds: 8, travelTime: 360.0 }
        });
      } else if (req.url.includes('specialism=Neurologie')) {
        req.reply({ statusCode: 404, body: {} });
      }
    }).as('getHospital');

    cy.visit('http://localhost:5173');
  });

  it("Scénario 1 : Recherche réussie avec affichage du temps réel", () => {
    cy.get('input').first().type('ambulance');
    cy.get('input[type="password"]').type('root');
    cy.contains('button', 'Se connecter').click();
    
    cy.get('select').first().select('Cardiologie');
    cy.contains('button', "Trouver l'hôpital le plus proche").click();
    
    cy.wait('@getHospital');
    cy.get('.result-card').should('contain', 'London Central Emergency');
    cy.get('.result-card').should('contain', '6 minutes'); // Vérifie la conversion OSRM
  });

  it("Scénario 2 : Affichage d'erreur si aucun hôpital n'est trouvé", () => {
    cy.get('input').first().type('ambulance');
    cy.get('input[type="password"]').type('root');
    cy.contains('button', 'Se connecter').click();
    
    cy.get('select').first().select('Neurologie');
    cy.contains('button', "Trouver l'hôpital le plus proche").click();
    
    cy.wait('@getHospital');
    cy.get('.alert-danger').should('contain', 'Aucun hôpital disponible');
  });

  it("Scénario 3 : Déconnexion de l'opérateur", () => {
    // Connexion
    cy.get('input').first().type('ambulance');
    cy.get('input[type="password"]').type('root');
    cy.contains('button', 'Se connecter').click();
    
    // Vérifie qu'on est connecté
    cy.contains('h2', "Système d'Affectation d'Urgence").should('be.visible');

    // Clic sur déconnexion
    cy.contains('button', 'Se déconnecter').click();

    // Vérifie le retour à l'écran d'accueil
    cy.contains('div', "Accès - Opérateur d'urgence").should('be.visible');
    cy.get('input').first().should('have.value', '');
  });
});