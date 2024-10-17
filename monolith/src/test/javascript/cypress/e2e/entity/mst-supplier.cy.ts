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

describe('MstSupplier e2e test', () => {
  const mstSupplierPageUrl = '/mst-supplier';
  const mstSupplierPageUrlPattern = new RegExp('/mst-supplier(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstSupplierSample = { name: 'why tromp nor' };

  let mstSupplier;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-suppliers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-suppliers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-suppliers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstSupplier) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-suppliers/${mstSupplier.id}`,
      }).then(() => {
        mstSupplier = undefined;
      });
    }
  });

  it('MstSuppliers menu should load MstSuppliers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-supplier');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstSupplier').should('exist');
    cy.url().should('match', mstSupplierPageUrlPattern);
  });

  describe('MstSupplier page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstSupplierPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstSupplier page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-supplier/new$'));
        cy.getEntityCreateUpdateHeading('MstSupplier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSupplierPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-suppliers',
          body: mstSupplierSample,
        }).then(({ body }) => {
          mstSupplier = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-suppliers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-suppliers?page=0&size=20>; rel="last",<http://localhost/api/mst-suppliers?page=0&size=20>; rel="first"',
              },
              body: [mstSupplier],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstSupplierPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstSupplier page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstSupplier');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSupplierPageUrlPattern);
      });

      it('edit button click should load edit MstSupplier page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstSupplier');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSupplierPageUrlPattern);
      });

      it.skip('edit button click should load edit MstSupplier page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstSupplier');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSupplierPageUrlPattern);
      });

      it('last delete button click should delete instance of MstSupplier', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstSupplier').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSupplierPageUrlPattern);

        mstSupplier = undefined;
      });
    });
  });

  describe('new MstSupplier page', () => {
    beforeEach(() => {
      cy.visit(`${mstSupplierPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstSupplier');
    });

    it('should create an instance of MstSupplier', () => {
      cy.get(`[data-cy="name"]`).type('apt');
      cy.get(`[data-cy="name"]`).should('have.value', 'apt');

      cy.get(`[data-cy="contactInfo"]`).type('because');
      cy.get(`[data-cy="contactInfo"]`).should('have.value', 'because');

      cy.get(`[data-cy="address"]`).type('nullify however');
      cy.get(`[data-cy="address"]`).should('have.value', 'nullify however');

      cy.get(`[data-cy="rating"]`).type('3');
      cy.get(`[data-cy="rating"]`).should('have.value', '3');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstSupplier = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstSupplierPageUrlPattern);
    });
  });
});
