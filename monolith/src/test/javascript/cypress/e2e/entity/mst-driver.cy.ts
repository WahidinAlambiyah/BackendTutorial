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

describe('MstDriver e2e test', () => {
  const mstDriverPageUrl = '/mst-driver';
  const mstDriverPageUrlPattern = new RegExp('/mst-driver(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstDriverSample = { name: 'opposite' };

  let mstDriver;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-drivers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-drivers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-drivers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstDriver) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-drivers/${mstDriver.id}`,
      }).then(() => {
        mstDriver = undefined;
      });
    }
  });

  it('MstDrivers menu should load MstDrivers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-driver');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstDriver').should('exist');
    cy.url().should('match', mstDriverPageUrlPattern);
  });

  describe('MstDriver page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstDriverPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstDriver page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-driver/new$'));
        cy.getEntityCreateUpdateHeading('MstDriver');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDriverPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-drivers',
          body: mstDriverSample,
        }).then(({ body }) => {
          mstDriver = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-drivers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-drivers?page=0&size=20>; rel="last",<http://localhost/api/mst-drivers?page=0&size=20>; rel="first"',
              },
              body: [mstDriver],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstDriverPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstDriver page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstDriver');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDriverPageUrlPattern);
      });

      it('edit button click should load edit MstDriver page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstDriver');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDriverPageUrlPattern);
      });

      it.skip('edit button click should load edit MstDriver page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstDriver');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDriverPageUrlPattern);
      });

      it('last delete button click should delete instance of MstDriver', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstDriver').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDriverPageUrlPattern);

        mstDriver = undefined;
      });
    });
  });

  describe('new MstDriver page', () => {
    beforeEach(() => {
      cy.visit(`${mstDriverPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstDriver');
    });

    it('should create an instance of MstDriver', () => {
      cy.get(`[data-cy="name"]`).type('dimple');
      cy.get(`[data-cy="name"]`).should('have.value', 'dimple');

      cy.get(`[data-cy="contactNumber"]`).type('lest');
      cy.get(`[data-cy="contactNumber"]`).should('have.value', 'lest');

      cy.get(`[data-cy="vehicleDetails"]`).type('eleventh');
      cy.get(`[data-cy="vehicleDetails"]`).should('have.value', 'eleventh');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstDriver = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstDriverPageUrlPattern);
    });
  });
});
