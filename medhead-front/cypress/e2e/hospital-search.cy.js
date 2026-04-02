describe("Recherche d'hôpital depuis l'interface", () => {

  beforeEach(() => {
    cy.intercept('GET', 'http://localhost:8080/api/hospitals/nearest*', (req) => {
      if (req.url.includes('specialism=Cardiologie')) {
        req.reply({
          statusCode: 200,
          body: {
            id: 1,
            name: "Hôpital Central de Test",
            availableBeds: 5,
            latitude: 48.85,
            longitude: 2.35
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

  it("doit afficher l'hôpital trouvé lorsque la recherche réussit", () => {
    cy.get('select.form-select').select('Cardiologie');
    
    cy.contains('button', "Trouver l'hôpital le plus proche").click();
    cy.wait('@getHospital');

    cy.get('.result-card').should('be.visible');
    cy.get('.result-card').should('contain', 'Hôpital Central de Test');
    cy.get('.result-card').should('contain', '5');
  });

  it("doit afficher un message d'erreur lorsqu'aucun hôpital n'est trouvé", () => {
    cy.get('select.form-select').select('Neurologie');
    
    cy.contains('button', "Trouver l'hôpital le plus proche").click();

    cy.wait('@getHospital');

    cy.get('.alert-danger').should('be.visible');
    cy.get('.alert-danger').should('contain', 'Aucun hôpital trouvé avec des lits disponibles pour cette spécialité.');
  });
});