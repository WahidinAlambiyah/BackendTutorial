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

describe('TrxDiscount e2e test', () => {
  const trxDiscountPageUrl = '/trx-discount';
  const trxDiscountPageUrlPattern = new RegExp('/trx-discount(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxDiscountSample = { discountPercentage: 8182.18, startDate: '2024-10-16T17:43:27.804Z', endDate: '2024-10-16T19:06:49.042Z' };

  let trxDiscount;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-discounts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-discounts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-discounts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxDiscount) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-discounts/${trxDiscount.id}`,
      }).then(() => {
        trxDiscount = undefined;
      });
    }
  });

  it('TrxDiscounts menu should load TrxDiscounts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-discount');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxDiscount').should('exist');
    cy.url().should('match', trxDiscountPageUrlPattern);
  });

  describe('TrxDiscount page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxDiscountPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxDiscount page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-discount/new$'));
        cy.getEntityCreateUpdateHeading('TrxDiscount');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDiscountPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-discounts',
          body: trxDiscountSample,
        }).then(({ body }) => {
          trxDiscount = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-discounts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-discounts?page=0&size=20>; rel="last",<http://localhost/api/trx-discounts?page=0&size=20>; rel="first"',
              },
              body: [trxDiscount],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxDiscountPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxDiscount page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxDiscount');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDiscountPageUrlPattern);
      });

      it('edit button click should load edit TrxDiscount page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxDiscount');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDiscountPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxDiscount page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxDiscount');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDiscountPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxDiscount', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxDiscount').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxDiscountPageUrlPattern);

        trxDiscount = undefined;
      });
    });
  });

  describe('new TrxDiscount page', () => {
    beforeEach(() => {
      cy.visit(`${trxDiscountPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxDiscount');
    });

    it('should create an instance of TrxDiscount', () => {
      cy.get(`[data-cy="discountPercentage"]`).type('20885.55');
      cy.get(`[data-cy="discountPercentage"]`).should('have.value', '20885.55');

      cy.get(`[data-cy="startDate"]`).type('2024-10-17T04:18');
      cy.get(`[data-cy="startDate"]`).blur();
      cy.get(`[data-cy="startDate"]`).should('have.value', '2024-10-17T04:18');

      cy.get(`[data-cy="endDate"]`).type('2024-10-17T03:30');
      cy.get(`[data-cy="endDate"]`).blur();
      cy.get(`[data-cy="endDate"]`).should('have.value', '2024-10-17T03:30');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxDiscount = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxDiscountPageUrlPattern);
    });
  });
});
