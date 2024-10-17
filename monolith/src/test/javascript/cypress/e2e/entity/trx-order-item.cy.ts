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

describe('TrxOrderItem e2e test', () => {
  const trxOrderItemPageUrl = '/trx-order-item';
  const trxOrderItemPageUrlPattern = new RegExp('/trx-order-item(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxOrderItemSample = { quantity: 25822, price: 12528.58 };

  let trxOrderItem;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-order-items+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-order-items').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-order-items/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxOrderItem) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-order-items/${trxOrderItem.id}`,
      }).then(() => {
        trxOrderItem = undefined;
      });
    }
  });

  it('TrxOrderItems menu should load TrxOrderItems page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-order-item');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxOrderItem').should('exist');
    cy.url().should('match', trxOrderItemPageUrlPattern);
  });

  describe('TrxOrderItem page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxOrderItemPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxOrderItem page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-order-item/new$'));
        cy.getEntityCreateUpdateHeading('TrxOrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderItemPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-order-items',
          body: trxOrderItemSample,
        }).then(({ body }) => {
          trxOrderItem = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-order-items+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-order-items?page=0&size=20>; rel="last",<http://localhost/api/trx-order-items?page=0&size=20>; rel="first"',
              },
              body: [trxOrderItem],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxOrderItemPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxOrderItem page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxOrderItem');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderItemPageUrlPattern);
      });

      it('edit button click should load edit TrxOrderItem page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxOrderItem');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderItemPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxOrderItem page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxOrderItem');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderItemPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxOrderItem', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxOrderItem').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderItemPageUrlPattern);

        trxOrderItem = undefined;
      });
    });
  });

  describe('new TrxOrderItem page', () => {
    beforeEach(() => {
      cy.visit(`${trxOrderItemPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxOrderItem');
    });

    it('should create an instance of TrxOrderItem', () => {
      cy.get(`[data-cy="quantity"]`).type('5817');
      cy.get(`[data-cy="quantity"]`).should('have.value', '5817');

      cy.get(`[data-cy="price"]`).type('25867.82');
      cy.get(`[data-cy="price"]`).should('have.value', '25867.82');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxOrderItem = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxOrderItemPageUrlPattern);
    });
  });
});
