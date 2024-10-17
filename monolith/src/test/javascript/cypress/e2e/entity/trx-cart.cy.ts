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

describe('TrxCart e2e test', () => {
  const trxCartPageUrl = '/trx-cart';
  const trxCartPageUrlPattern = new RegExp('/trx-cart(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxCartSample = { totalPrice: 3997.89 };

  let trxCart;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-carts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-carts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-carts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxCart) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-carts/${trxCart.id}`,
      }).then(() => {
        trxCart = undefined;
      });
    }
  });

  it('TrxCarts menu should load TrxCarts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-cart');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxCart').should('exist');
    cy.url().should('match', trxCartPageUrlPattern);
  });

  describe('TrxCart page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxCartPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxCart page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-cart/new$'));
        cy.getEntityCreateUpdateHeading('TrxCart');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCartPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-carts',
          body: trxCartSample,
        }).then(({ body }) => {
          trxCart = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-carts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-carts?page=0&size=20>; rel="last",<http://localhost/api/trx-carts?page=0&size=20>; rel="first"',
              },
              body: [trxCart],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxCartPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxCart page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxCart');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCartPageUrlPattern);
      });

      it('edit button click should load edit TrxCart page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxCart');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCartPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxCart page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxCart');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCartPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxCart', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxCart').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCartPageUrlPattern);

        trxCart = undefined;
      });
    });
  });

  describe('new TrxCart page', () => {
    beforeEach(() => {
      cy.visit(`${trxCartPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxCart');
    });

    it('should create an instance of TrxCart', () => {
      cy.get(`[data-cy="totalPrice"]`).type('8540.93');
      cy.get(`[data-cy="totalPrice"]`).should('have.value', '8540.93');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxCart = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxCartPageUrlPattern);
    });
  });
});
