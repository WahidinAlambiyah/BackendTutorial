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

describe('MstService e2e test', () => {
  const mstServicePageUrl = '/mst-service';
  const mstServicePageUrlPattern = new RegExp('/mst-service(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstServiceSample = { name: 'loftily' };

  let mstService;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-services+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-services').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-services/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstService) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-services/${mstService.id}`,
      }).then(() => {
        mstService = undefined;
      });
    }
  });

  it('MstServices menu should load MstServices page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-service');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstService').should('exist');
    cy.url().should('match', mstServicePageUrlPattern);
  });

  describe('MstService page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstServicePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstService page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-service/new$'));
        cy.getEntityCreateUpdateHeading('MstService');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstServicePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-services',
          body: mstServiceSample,
        }).then(({ body }) => {
          mstService = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-services+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-services?page=0&size=20>; rel="last",<http://localhost/api/mst-services?page=0&size=20>; rel="first"',
              },
              body: [mstService],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstServicePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstService page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstService');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstServicePageUrlPattern);
      });

      it('edit button click should load edit MstService page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstService');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstServicePageUrlPattern);
      });

      it.skip('edit button click should load edit MstService page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstService');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstServicePageUrlPattern);
      });

      it('last delete button click should delete instance of MstService', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstService').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstServicePageUrlPattern);

        mstService = undefined;
      });
    });
  });

  describe('new MstService page', () => {
    beforeEach(() => {
      cy.visit(`${mstServicePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstService');
    });

    it('should create an instance of MstService', () => {
      cy.get(`[data-cy="name"]`).type('mostly er neuropsychiatry');
      cy.get(`[data-cy="name"]`).should('have.value', 'mostly er neuropsychiatry');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="price"]`).type('26024.4');
      cy.get(`[data-cy="price"]`).should('have.value', '26024.4');

      cy.get(`[data-cy="durationInHours"]`).type('14242');
      cy.get(`[data-cy="durationInHours"]`).should('have.value', '14242');

      cy.get(`[data-cy="serviceType"]`).select('TICKETING');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstService = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstServicePageUrlPattern);
    });
  });
});
