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

describe('TrxDelivery e2e test', () => {
  const trxDeliveryPageUrl = '/trx-delivery';
  const trxDeliveryPageUrlPattern = new RegExp('/trx-delivery(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxDeliverySample = { deliveryAddress: 'incidentally creativity', deliveryStatus: 'DELIVERED' };

  let trxDelivery;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-deliveries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-deliveries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-deliveries/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxDelivery) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-deliveries/${trxDelivery.id}`,
      }).then(() => {
        trxDelivery = undefined;
      });
    }
  });

  it('TrxDeliveries menu should load TrxDeliveries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-delivery');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxDelivery').should('exist');
    cy.url().should('match', trxDeliveryPageUrlPattern);
  });

  describe('TrxDelivery page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxDeliveryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxDelivery page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-delivery/new$'));
        cy.getEntityCreateUpdateHeading('TrxDelivery');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDeliveryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-deliveries',
          body: trxDeliverySample,
        }).then(({ body }) => {
          trxDelivery = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-deliveries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-deliveries?page=0&size=20>; rel="last",<http://localhost/api/trx-deliveries?page=0&size=20>; rel="first"',
              },
              body: [trxDelivery],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxDeliveryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxDelivery page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxDelivery');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDeliveryPageUrlPattern);
      });

      it('edit button click should load edit TrxDelivery page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxDelivery');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDeliveryPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxDelivery page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxDelivery');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDeliveryPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxDelivery', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxDelivery').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDeliveryPageUrlPattern);

        trxDelivery = undefined;
      });
    });
  });

  describe('new TrxDelivery page', () => {
    beforeEach(() => {
      cy.visit(`${trxDeliveryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxDelivery');
    });

    it('should create an instance of TrxDelivery', () => {
      cy.get(`[data-cy="deliveryAddress"]`).type('selfishly unlike');
      cy.get(`[data-cy="deliveryAddress"]`).should('have.value', 'selfishly unlike');

      cy.get(`[data-cy="deliveryStatus"]`).select('OUT_FOR_DELIVERY');

      cy.get(`[data-cy="assignedDriver"]`).type('anti');
      cy.get(`[data-cy="assignedDriver"]`).should('have.value', 'anti');

      cy.get(`[data-cy="estimatedDeliveryTime"]`).type('2024-10-16T19:27');
      cy.get(`[data-cy="estimatedDeliveryTime"]`).blur();
      cy.get(`[data-cy="estimatedDeliveryTime"]`).should('have.value', '2024-10-16T19:27');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxDelivery = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxDeliveryPageUrlPattern);
    });
  });
});
