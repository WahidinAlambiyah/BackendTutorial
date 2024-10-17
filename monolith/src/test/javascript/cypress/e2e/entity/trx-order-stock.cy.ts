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

describe('TrxOrderStock e2e test', () => {
  const trxOrderStockPageUrl = '/trx-order-stock';
  const trxOrderStockPageUrlPattern = new RegExp('/trx-order-stock(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxOrderStockSample = { quantityOrdered: 19917, orderDate: '2024-10-16T12:06:27.509Z' };

  let trxOrderStock;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-order-stocks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-order-stocks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-order-stocks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxOrderStock) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-order-stocks/${trxOrderStock.id}`,
      }).then(() => {
        trxOrderStock = undefined;
      });
    }
  });

  it('TrxOrderStocks menu should load TrxOrderStocks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-order-stock');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxOrderStock').should('exist');
    cy.url().should('match', trxOrderStockPageUrlPattern);
  });

  describe('TrxOrderStock page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxOrderStockPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxOrderStock page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-order-stock/new$'));
        cy.getEntityCreateUpdateHeading('TrxOrderStock');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderStockPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-order-stocks',
          body: trxOrderStockSample,
        }).then(({ body }) => {
          trxOrderStock = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-order-stocks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-order-stocks?page=0&size=20>; rel="last",<http://localhost/api/trx-order-stocks?page=0&size=20>; rel="first"',
              },
              body: [trxOrderStock],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxOrderStockPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxOrderStock page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxOrderStock');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderStockPageUrlPattern);
      });

      it('edit button click should load edit TrxOrderStock page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxOrderStock');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderStockPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxOrderStock page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxOrderStock');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderStockPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxOrderStock', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxOrderStock').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderStockPageUrlPattern);

        trxOrderStock = undefined;
      });
    });
  });

  describe('new TrxOrderStock page', () => {
    beforeEach(() => {
      cy.visit(`${trxOrderStockPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxOrderStock');
    });

    it('should create an instance of TrxOrderStock', () => {
      cy.get(`[data-cy="quantityOrdered"]`).type('20590');
      cy.get(`[data-cy="quantityOrdered"]`).should('have.value', '20590');

      cy.get(`[data-cy="orderDate"]`).type('2024-10-16T13:27');
      cy.get(`[data-cy="orderDate"]`).blur();
      cy.get(`[data-cy="orderDate"]`).should('have.value', '2024-10-16T13:27');

      cy.get(`[data-cy="expectedArrivalDate"]`).type('2024-10-16T23:42');
      cy.get(`[data-cy="expectedArrivalDate"]`).blur();
      cy.get(`[data-cy="expectedArrivalDate"]`).should('have.value', '2024-10-16T23:42');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxOrderStock = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxOrderStockPageUrlPattern);
    });
  });
});
