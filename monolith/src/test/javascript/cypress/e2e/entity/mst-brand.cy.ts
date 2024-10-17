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

describe('MstBrand e2e test', () => {
  const mstBrandPageUrl = '/mst-brand';
  const mstBrandPageUrlPattern = new RegExp('/mst-brand(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstBrandSample = { name: 'voluntarily' };

  let mstBrand;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-brands+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-brands').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-brands/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstBrand) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-brands/${mstBrand.id}`,
      }).then(() => {
        mstBrand = undefined;
      });
    }
  });

  it('MstBrands menu should load MstBrands page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-brand');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstBrand').should('exist');
    cy.url().should('match', mstBrandPageUrlPattern);
  });

  describe('MstBrand page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstBrandPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstBrand page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-brand/new$'));
        cy.getEntityCreateUpdateHeading('MstBrand');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstBrandPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-brands',
          body: mstBrandSample,
        }).then(({ body }) => {
          mstBrand = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-brands+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-brands?page=0&size=20>; rel="last",<http://localhost/api/mst-brands?page=0&size=20>; rel="first"',
              },
              body: [mstBrand],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstBrandPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstBrand page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstBrand');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstBrandPageUrlPattern);
      });

      it('edit button click should load edit MstBrand page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstBrand');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstBrandPageUrlPattern);
      });

      it.skip('edit button click should load edit MstBrand page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstBrand');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstBrandPageUrlPattern);
      });

      it('last delete button click should delete instance of MstBrand', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstBrand').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstBrandPageUrlPattern);

        mstBrand = undefined;
      });
    });
  });

  describe('new MstBrand page', () => {
    beforeEach(() => {
      cy.visit(`${mstBrandPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstBrand');
    });

    it('should create an instance of MstBrand', () => {
      cy.get(`[data-cy="name"]`).type('hm utensil taxi');
      cy.get(`[data-cy="name"]`).should('have.value', 'hm utensil taxi');

      cy.get(`[data-cy="logo"]`).type('which');
      cy.get(`[data-cy="logo"]`).should('have.value', 'which');

      cy.get(`[data-cy="description"]`).type('feminine disloyal modem');
      cy.get(`[data-cy="description"]`).should('have.value', 'feminine disloyal modem');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstBrand = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstBrandPageUrlPattern);
    });
  });
});
