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

describe('MstCategory e2e test', () => {
  const mstCategoryPageUrl = '/mst-category';
  const mstCategoryPageUrlPattern = new RegExp('/mst-category(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstCategorySample = { name: 'venture easy-going artistic' };

  let mstCategory;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-categories+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-categories').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-categories/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstCategory) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-categories/${mstCategory.id}`,
      }).then(() => {
        mstCategory = undefined;
      });
    }
  });

  it('MstCategories menu should load MstCategories page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-category');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstCategory').should('exist');
    cy.url().should('match', mstCategoryPageUrlPattern);
  });

  describe('MstCategory page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstCategoryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstCategory page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-category/new$'));
        cy.getEntityCreateUpdateHeading('MstCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCategoryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-categories',
          body: mstCategorySample,
        }).then(({ body }) => {
          mstCategory = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-categories+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-categories?page=0&size=20>; rel="last",<http://localhost/api/mst-categories?page=0&size=20>; rel="first"',
              },
              body: [mstCategory],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstCategoryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstCategory page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstCategory');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCategoryPageUrlPattern);
      });

      it('edit button click should load edit MstCategory page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstCategory');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCategoryPageUrlPattern);
      });

      it.skip('edit button click should load edit MstCategory page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstCategory');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCategoryPageUrlPattern);
      });

      it('last delete button click should delete instance of MstCategory', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstCategory').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCategoryPageUrlPattern);

        mstCategory = undefined;
      });
    });
  });

  describe('new MstCategory page', () => {
    beforeEach(() => {
      cy.visit(`${mstCategoryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstCategory');
    });

    it('should create an instance of MstCategory', () => {
      cy.get(`[data-cy="name"]`).type('aha');
      cy.get(`[data-cy="name"]`).should('have.value', 'aha');

      cy.get(`[data-cy="description"]`).type('avoid delayed finally');
      cy.get(`[data-cy="description"]`).should('have.value', 'avoid delayed finally');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstCategory = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstCategoryPageUrlPattern);
    });
  });
});
