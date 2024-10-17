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

describe('TrxOrderHistory e2e test', () => {
  const trxOrderHistoryPageUrl = '/trx-order-history';
  const trxOrderHistoryPageUrlPattern = new RegExp('/trx-order-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxOrderHistorySample = { previousStatus: 'COMPLETED', newStatus: 'COMPLETED', changeDate: '2024-10-16T12:21:48.639Z' };

  let trxOrderHistory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-order-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-order-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-order-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxOrderHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-order-histories/${trxOrderHistory.id}`,
      }).then(() => {
        trxOrderHistory = undefined;
      });
    }
  });

  it('TrxOrderHistories menu should load TrxOrderHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-order-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxOrderHistory').should('exist');
    cy.url().should('match', trxOrderHistoryPageUrlPattern);
  });

  describe('TrxOrderHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxOrderHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxOrderHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-order-history/new$'));
        cy.getEntityCreateUpdateHeading('TrxOrderHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-order-histories',
          body: trxOrderHistorySample,
        }).then(({ body }) => {
          trxOrderHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-order-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-order-histories?page=0&size=20>; rel="last",<http://localhost/api/trx-order-histories?page=0&size=20>; rel="first"',
              },
              body: [trxOrderHistory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxOrderHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxOrderHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxOrderHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderHistoryPageUrlPattern);
      });

      it('edit button click should load edit TrxOrderHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxOrderHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderHistoryPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxOrderHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxOrderHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxOrderHistory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxOrderHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderHistoryPageUrlPattern);

        trxOrderHistory = undefined;
      });
    });
  });

  describe('new TrxOrderHistory page', () => {
    beforeEach(() => {
      cy.visit(`${trxOrderHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxOrderHistory');
    });

    it('should create an instance of TrxOrderHistory', () => {
      cy.get(`[data-cy="previousStatus"]`).select('PENDING');

      cy.get(`[data-cy="newStatus"]`).select('CANCELLED');

      cy.get(`[data-cy="changeDate"]`).type('2024-10-16T23:18');
      cy.get(`[data-cy="changeDate"]`).blur();
      cy.get(`[data-cy="changeDate"]`).should('have.value', '2024-10-16T23:18');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxOrderHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxOrderHistoryPageUrlPattern);
    });
  });
});
