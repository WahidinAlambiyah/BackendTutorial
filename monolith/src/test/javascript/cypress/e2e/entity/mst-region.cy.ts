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

describe('MstRegion e2e test', () => {
  const mstRegionPageUrl = '/mst-region';
  const mstRegionPageUrlPattern = new RegExp('/mst-region(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstRegionSample = { name: 'of' };

  let mstRegion;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-regions+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-regions').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-regions/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstRegion) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-regions/${mstRegion.id}`,
      }).then(() => {
        mstRegion = undefined;
      });
    }
  });

  it('MstRegions menu should load MstRegions page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-region');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstRegion').should('exist');
    cy.url().should('match', mstRegionPageUrlPattern);
  });

  describe('MstRegion page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstRegionPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstRegion page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-region/new$'));
        cy.getEntityCreateUpdateHeading('MstRegion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstRegionPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-regions',
          body: mstRegionSample,
        }).then(({ body }) => {
          mstRegion = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-regions+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-regions?page=0&size=20>; rel="last",<http://localhost/api/mst-regions?page=0&size=20>; rel="first"',
              },
              body: [mstRegion],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstRegionPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstRegion page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstRegion');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstRegionPageUrlPattern);
      });

      it('edit button click should load edit MstRegion page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstRegion');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstRegionPageUrlPattern);
      });

      it.skip('edit button click should load edit MstRegion page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstRegion');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstRegionPageUrlPattern);
      });

      it('last delete button click should delete instance of MstRegion', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstRegion').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstRegionPageUrlPattern);

        mstRegion = undefined;
      });
    });
  });

  describe('new MstRegion page', () => {
    beforeEach(() => {
      cy.visit(`${mstRegionPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstRegion');
    });

    it('should create an instance of MstRegion', () => {
      cy.get(`[data-cy="name"]`).type('meanwhile animated neglected');
      cy.get(`[data-cy="name"]`).should('have.value', 'meanwhile animated neglected');

      cy.get(`[data-cy="unm49Code"]`).type('cleverly so');
      cy.get(`[data-cy="unm49Code"]`).should('have.value', 'cleverly so');

      cy.get(`[data-cy="isoAlpha2Code"]`).type('amid');
      cy.get(`[data-cy="isoAlpha2Code"]`).should('have.value', 'amid');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstRegion = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstRegionPageUrlPattern);
    });
  });
});
