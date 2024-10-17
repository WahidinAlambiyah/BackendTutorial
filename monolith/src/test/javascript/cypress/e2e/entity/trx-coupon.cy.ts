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

describe('TrxCoupon e2e test', () => {
  const trxCouponPageUrl = '/trx-coupon';
  const trxCouponPageUrlPattern = new RegExp('/trx-coupon(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxCouponSample = { code: 'off astride', discountAmount: 24502.38, validUntil: '2024-10-17T06:07:17.307Z' };

  let trxCoupon;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-coupons+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-coupons').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-coupons/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxCoupon) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-coupons/${trxCoupon.id}`,
      }).then(() => {
        trxCoupon = undefined;
      });
    }
  });

  it('TrxCoupons menu should load TrxCoupons page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-coupon');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxCoupon').should('exist');
    cy.url().should('match', trxCouponPageUrlPattern);
  });

  describe('TrxCoupon page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxCouponPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxCoupon page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-coupon/new$'));
        cy.getEntityCreateUpdateHeading('TrxCoupon');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCouponPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-coupons',
          body: trxCouponSample,
        }).then(({ body }) => {
          trxCoupon = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-coupons+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-coupons?page=0&size=20>; rel="last",<http://localhost/api/trx-coupons?page=0&size=20>; rel="first"',
              },
              body: [trxCoupon],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxCouponPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxCoupon page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxCoupon');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCouponPageUrlPattern);
      });

      it('edit button click should load edit TrxCoupon page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxCoupon');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCouponPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxCoupon page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxCoupon');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCouponPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxCoupon', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxCoupon').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxCouponPageUrlPattern);

        trxCoupon = undefined;
      });
    });
  });

  describe('new TrxCoupon page', () => {
    beforeEach(() => {
      cy.visit(`${trxCouponPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxCoupon');
    });

    it('should create an instance of TrxCoupon', () => {
      cy.get(`[data-cy="code"]`).type('wan heavily consequently');
      cy.get(`[data-cy="code"]`).should('have.value', 'wan heavily consequently');

      cy.get(`[data-cy="discountAmount"]`).type('7305.76');
      cy.get(`[data-cy="discountAmount"]`).should('have.value', '7305.76');

      cy.get(`[data-cy="validUntil"]`).type('2024-10-16T14:59');
      cy.get(`[data-cy="validUntil"]`).blur();
      cy.get(`[data-cy="validUntil"]`).should('have.value', '2024-10-16T14:59');

      cy.get(`[data-cy="minPurchase"]`).type('25685.72');
      cy.get(`[data-cy="minPurchase"]`).should('have.value', '25685.72');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxCoupon = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxCouponPageUrlPattern);
    });
  });
});
