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

describe('MstProduct e2e test', () => {
  const mstProductPageUrl = '/mst-product';
  const mstProductPageUrlPattern = new RegExp('/mst-product(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstProductSample = { name: 'wisdom stub fidget', price: 30612.17, quantity: 10147 };

  let mstProduct;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-products+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-products').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-products/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstProduct) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-products/${mstProduct.id}`,
      }).then(() => {
        mstProduct = undefined;
      });
    }
  });

  it('MstProducts menu should load MstProducts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-product');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstProduct').should('exist');
    cy.url().should('match', mstProductPageUrlPattern);
  });

  describe('MstProduct page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstProductPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstProduct page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-product/new$'));
        cy.getEntityCreateUpdateHeading('MstProduct');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProductPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-products',
          body: mstProductSample,
        }).then(({ body }) => {
          mstProduct = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-products+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-products?page=0&size=20>; rel="last",<http://localhost/api/mst-products?page=0&size=20>; rel="first"',
              },
              body: [mstProduct],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstProductPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstProduct page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstProduct');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProductPageUrlPattern);
      });

      it('edit button click should load edit MstProduct page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstProduct');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProductPageUrlPattern);
      });

      it.skip('edit button click should load edit MstProduct page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstProduct');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProductPageUrlPattern);
      });

      it('last delete button click should delete instance of MstProduct', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstProduct').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProductPageUrlPattern);

        mstProduct = undefined;
      });
    });
  });

  describe('new MstProduct page', () => {
    beforeEach(() => {
      cy.visit(`${mstProductPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstProduct');
    });

    it('should create an instance of MstProduct', () => {
      cy.get(`[data-cy="name"]`).type('swiftly');
      cy.get(`[data-cy="name"]`).should('have.value', 'swiftly');

      cy.get(`[data-cy="description"]`).type('aside');
      cy.get(`[data-cy="description"]`).should('have.value', 'aside');

      cy.get(`[data-cy="price"]`).type('5700.79');
      cy.get(`[data-cy="price"]`).should('have.value', '5700.79');

      cy.get(`[data-cy="quantity"]`).type('16742');
      cy.get(`[data-cy="quantity"]`).should('have.value', '16742');

      cy.get(`[data-cy="barcode"]`).type('blindly');
      cy.get(`[data-cy="barcode"]`).should('have.value', 'blindly');

      cy.get(`[data-cy="unitSize"]`).type('bah coolly');
      cy.get(`[data-cy="unitSize"]`).should('have.value', 'bah coolly');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstProduct = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstProductPageUrlPattern);
    });
  });
});
