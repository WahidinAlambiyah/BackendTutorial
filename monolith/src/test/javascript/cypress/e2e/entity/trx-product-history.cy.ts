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

describe('TrxProductHistory e2e test', () => {
  const trxProductHistoryPageUrl = '/trx-product-history';
  const trxProductHistoryPageUrlPattern = new RegExp('/trx-product-history(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxProductHistorySample = { changeDate: '2024-10-16T21:18:49.216Z' };

  let trxProductHistory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-product-histories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-product-histories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-product-histories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxProductHistory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-product-histories/${trxProductHistory.id}`,
      }).then(() => {
        trxProductHistory = undefined;
      });
    }
  });

  it('TrxProductHistories menu should load TrxProductHistories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-product-history');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxProductHistory').should('exist');
    cy.url().should('match', trxProductHistoryPageUrlPattern);
  });

  describe('TrxProductHistory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxProductHistoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxProductHistory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-product-history/new$'));
        cy.getEntityCreateUpdateHeading('TrxProductHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxProductHistoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-product-histories',
          body: trxProductHistorySample,
        }).then(({ body }) => {
          trxProductHistory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-product-histories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-product-histories?page=0&size=20>; rel="last",<http://localhost/api/trx-product-histories?page=0&size=20>; rel="first"',
              },
              body: [trxProductHistory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxProductHistoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxProductHistory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxProductHistory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxProductHistoryPageUrlPattern);
      });

      it('edit button click should load edit TrxProductHistory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxProductHistory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxProductHistoryPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxProductHistory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxProductHistory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxProductHistoryPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxProductHistory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxProductHistory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxProductHistoryPageUrlPattern);

        trxProductHistory = undefined;
      });
    });
  });

  describe('new TrxProductHistory page', () => {
    beforeEach(() => {
      cy.visit(`${trxProductHistoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxProductHistory');
    });

    it('should create an instance of TrxProductHistory', () => {
      cy.get(`[data-cy="oldPrice"]`).type('26014.63');
      cy.get(`[data-cy="oldPrice"]`).should('have.value', '26014.63');

      cy.get(`[data-cy="newPrice"]`).type('10397.22');
      cy.get(`[data-cy="newPrice"]`).should('have.value', '10397.22');

      cy.get(`[data-cy="changeDate"]`).type('2024-10-16T09:04');
      cy.get(`[data-cy="changeDate"]`).blur();
      cy.get(`[data-cy="changeDate"]`).should('have.value', '2024-10-16T09:04');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxProductHistory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxProductHistoryPageUrlPattern);
    });
  });
});
