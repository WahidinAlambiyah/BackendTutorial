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

describe('TrxTournament e2e test', () => {
  const trxTournamentPageUrl = '/trx-tournament';
  const trxTournamentPageUrlPattern = new RegExp('/trx-tournament(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxTournamentSample = { name: 'dirty whoa', startDate: '2024-09-24T04:16:33.691Z', endDate: '2024-09-24T05:37:56.943Z' };

  let trxTournament;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-tournaments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-tournaments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-tournaments/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxTournament) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-tournaments/${trxTournament.id}`,
      }).then(() => {
        trxTournament = undefined;
      });
    }
  });

  it('TrxTournaments menu should load TrxTournaments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-tournament');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxTournament').should('exist');
    cy.url().should('match', trxTournamentPageUrlPattern);
  });

  describe('TrxTournament page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxTournamentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxTournament page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-tournament/new$'));
        cy.getEntityCreateUpdateHeading('TrxTournament');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTournamentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-tournaments',
          body: trxTournamentSample,
        }).then(({ body }) => {
          trxTournament = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-tournaments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-tournaments?page=0&size=20>; rel="last",<http://localhost/api/trx-tournaments?page=0&size=20>; rel="first"',
              },
              body: [trxTournament],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxTournamentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxTournament page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxTournament');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTournamentPageUrlPattern);
      });

      it('edit button click should load edit TrxTournament page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxTournament');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTournamentPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxTournament page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxTournament');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTournamentPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxTournament', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxTournament').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTournamentPageUrlPattern);

        trxTournament = undefined;
      });
    });
  });

  describe('new TrxTournament page', () => {
    beforeEach(() => {
      cy.visit(`${trxTournamentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxTournament');
    });

    it('should create an instance of TrxTournament', () => {
      cy.get(`[data-cy="name"]`).type('refurbish once smolt');
      cy.get(`[data-cy="name"]`).should('have.value', 'refurbish once smolt');

      cy.get(`[data-cy="type"]`).select('TEAM');

      cy.get(`[data-cy="prizeAmount"]`).type('29384.53');
      cy.get(`[data-cy="prizeAmount"]`).should('have.value', '29384.53');

      cy.get(`[data-cy="startDate"]`).type('2024-09-23T23:39');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2024-09-23T23:39');

      cy.get(`[data-cy="endDate"]`).type('2024-09-24T06:22');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2024-09-24T06:22');

      cy.get(`[data-cy="location"]`).type('traumatise beautifully trained');
      cy.get(`[data-cy="location"]`).should('have.value', 'traumatise beautifully trained');

      cy.get(`[data-cy="maxParticipants"]`).type('30323');
      cy.get(`[data-cy="maxParticipants"]`).should('have.value', '30323');

      cy.get(`[data-cy="status"]`).select('CANCELLED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxTournament = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxTournamentPageUrlPattern);
    });
  });
});
