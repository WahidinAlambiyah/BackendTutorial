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

describe('TrxOrder e2e test', () => {
  const trxOrderPageUrl = '/trx-order';
  const trxOrderPageUrlPattern = new RegExp('/trx-order(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxOrderSample = { orderDate: '2024-10-16T14:06:30.837Z', orderStatus: 'COMPLETED', paymentMethod: 'ONLINE', totalAmount: 6991 };

  let trxOrder;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-orders+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-orders').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-orders/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxOrder) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-orders/${trxOrder.id}`,
      }).then(() => {
        trxOrder = undefined;
      });
    }
  });

  it('TrxOrders menu should load TrxOrders page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-order');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxOrder').should('exist');
    cy.url().should('match', trxOrderPageUrlPattern);
  });

  describe('TrxOrder page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxOrderPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxOrder page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-order/new$'));
        cy.getEntityCreateUpdateHeading('TrxOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-orders',
          body: trxOrderSample,
        }).then(({ body }) => {
          trxOrder = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-orders+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-orders?page=0&size=20>; rel="last",<http://localhost/api/trx-orders?page=0&size=20>; rel="first"',
              },
              body: [trxOrder],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxOrderPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxOrder page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxOrder');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderPageUrlPattern);
      });

      it('edit button click should load edit TrxOrder page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxOrder');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxOrder page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxOrder');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxOrder', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxOrder').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxOrderPageUrlPattern);

        trxOrder = undefined;
      });
    });
  });

  describe('new TrxOrder page', () => {
    beforeEach(() => {
      cy.visit(`${trxOrderPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxOrder');
    });

    it('should create an instance of TrxOrder', () => {
      cy.get(`[data-cy="orderDate"]`).type('2024-10-17T02:14');
      cy.get(`[data-cy="orderDate"]`).blur();
      cy.get(`[data-cy="orderDate"]`).should('have.value', '2024-10-17T02:14');

      cy.get(`[data-cy="deliveryDate"]`).type('2024-10-16T18:00');
      cy.get(`[data-cy="deliveryDate"]`).blur();
      cy.get(`[data-cy="deliveryDate"]`).should('have.value', '2024-10-16T18:00');

      cy.get(`[data-cy="orderStatus"]`).select('PENDING');

      cy.get(`[data-cy="paymentMethod"]`).select('ONLINE');

      cy.get(`[data-cy="totalAmount"]`).type('19594.96');
      cy.get(`[data-cy="totalAmount"]`).should('have.value', '19594.96');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxOrder = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxOrderPageUrlPattern);
    });
  });
});
