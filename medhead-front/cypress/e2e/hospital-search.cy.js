describe("Recherche d'hôpital depuis l'interface", () => {

  beforeEach(() => {
    cy.intercept('GET', 'http://localhost:8080/api/hospitals/nearest*', (req) => {
      
      if (req.headers.authorization !== 'Basic YW1idWxhbmNlOnJvb3Q=') {
        req.reply({ statusCode: 401, body: "Unauthorized" });
        return;
      }

      if (req.url.includes('specialism=Cardiologie')) {
        req.reply({
          statusCode: 200,
          body: {
            id: 1,
            name: "Hôpital Central de Test",
            availableBeds: 5,
            latitude: 51.503, 
            longitude: -0.114
          }
        });
      } else if (req.url.includes('specialism=Neurologie')) {
        req.reply({
          statusCode: 404,
          body: "Not Found"
        });
      }
    }).as('getHospital');

    cy.visit('http://localhost:5173');
  });

  it("doit afficher une erreur et déconnecter si l'API rejette les identifiants (401)", () => {
    // Ciblage propre via le type d'input
    cy.get('input[type="text"]').type('hacker');
    cy.get('input[type="password"]').type('fakepassword');
    cy.contains('button', 'Se connecter').click();

    cy.contains('button', "Trouver l'hôpital le plus proche").click();
    cy.wait('@getHospital');

    cy.get('.alert-danger').should('be.visible');
    cy.get('.alert-danger').should('contain', "Erreur d'authentification");
    cy.contains('button', 'Se connecter').should('be.visible');
  });

  it("doit afficher l'hôpital trouvé lorsque la recherche réussit", () => {
    cy.get('input[type="text"]').type('ambulance');
    cy.get('input[type="password"]').type('root');
    cy.contains('button', 'Se connecter').click();
    
    cy.get('select').first().select('Cardiologie');
    
    cy.contains('button', "Trouver l'hôpital le plus proche").click();
    cy.wait('@getHospital');

    cy.get('.result-card').should('be.visible');
    cy.get('.result-card').should('contain', 'Hôpital Central de Test');
    cy.get('.result-card').should('contain', '5');
  });

  it("doit afficher un message d'erreur lorsqu'aucun hôpital n'est trouvé (404)", () => {
    cy.get('input[type="text"]').type('ambulance');
    cy.get('input[type="password"]').type('root');
    cy.contains('button', 'Se connecter').click();

    cy.get('select').first().select('Neurologie');
    
    cy.contains('button', "Trouver l'hôpital le plus proche").click();
    cy.wait('@getHospital');

    cy.get('.alert-danger').should('be.visible');
    cy.get('.alert-danger').should('contain', 'Aucun hôpital trouvé');
  });

  it("doit permettre à l'utilisateur de se déconnecter", () => {

    cy.get('input[type="text"]').type('ambulance');
    cy.get('input[type="password"]').type('root');
    cy.contains('button', 'Se connecter').click();
    cy.contains('h2', "Recherche d'Hôpital").should('be.visible');
    cy.contains('button', 'Se déconnecter').click();

    cy.contains('div', "Accès - Opérateur d'urgence").should('be.visible');
    cy.get('input[type="text"]').should('have.value', '');
    cy.get('input[type="password"]').should('have.value', '');
  });

});