import {
  entityTableSelector,
  entityDetailsButtonSelector,
  entityDetailsBackButtonSelector,
  entityCreateButtonSelector,
  entityCreateSaveButtonSelector,
  entityCreateCancelButtonSelector,
  entityEditButtonSelector,
  entityDeleteButtonSelector,
  entityConfirmDeleteButtonSelector,
} from '../../support/entity';

describe('Tournament e2e test', () => {
  const tournamentPageUrl = '/tournament';
  const tournamentPageUrlPattern = new RegExp('/tournament(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const tournamentSample = { name: 'across almost', startDate: '2024-09-13T23:35:47.353Z', endDate: '2024-09-14T11:10:07.522Z' };

  let tournament;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/tournaments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/tournaments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/tournaments/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (tournament) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/tournaments/${tournament.id}`,
      }).then(() => {
        tournament = undefined;
      });
    }
  });

  it('Tournaments menu should load Tournaments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('tournament');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Tournament').should('exist');
    cy.url().should('match', tournamentPageUrlPattern);
  });

  describe('Tournament page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(tournamentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Tournament page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/tournament/new$'));
        cy.getEntityCreateUpdateHeading('Tournament');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tournamentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/tournaments',
          body: tournamentSample,
        }).then(({ body }) => {
          tournament = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/tournaments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [tournament],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(tournamentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Tournament page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('tournament');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tournamentPageUrlPattern);
      });

      it('edit button click should load edit Tournament page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Tournament');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tournamentPageUrlPattern);
      });

      it('edit button click should load edit Tournament page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Tournament');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tournamentPageUrlPattern);
      });

      it('last delete button click should delete instance of Tournament', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('tournament').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', tournamentPageUrlPattern);

        tournament = undefined;
      });
    });
  });

  describe('new Tournament page', () => {
    beforeEach(() => {
      cy.visit(`${tournamentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Tournament');
    });

    it('should create an instance of Tournament', () => {
      cy.get(`[data-cy="name"]`).type('whoever shirk');
      cy.get(`[data-cy="name"]`).should('have.value', 'whoever shirk');

      cy.get(`[data-cy="type"]`).select('TEAM');

      cy.get(`[data-cy="prizeAmount"]`).type('23821.05');
      cy.get(`[data-cy="prizeAmount"]`).should('have.value', '23821.05');

      cy.get(`[data-cy="startDate"]`).type('2024-09-14T04:03');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2024-09-14T04:03');

      cy.get(`[data-cy="endDate"]`).type('2024-09-13T19:58');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2024-09-13T19:58');

      cy.get(`[data-cy="location"]`).type('crazy wrongly');
      cy.get(`[data-cy="location"]`).should('have.value', 'crazy wrongly');

      cy.get(`[data-cy="maxParticipants"]`).type('14160');
      cy.get(`[data-cy="maxParticipants"]`).should('have.value', '14160');

      cy.get(`[data-cy="status"]`).select('UPCOMING');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        tournament = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', tournamentPageUrlPattern);
    });
  });
});
