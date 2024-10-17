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

describe('MstCity e2e test', () => {
  const mstCityPageUrl = '/mst-city';
  const mstCityPageUrlPattern = new RegExp('/mst-city(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstCitySample = { name: 'bleakly' };

  let mstCity;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-cities+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-cities').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-cities/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstCity) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-cities/${mstCity.id}`,
      }).then(() => {
        mstCity = undefined;
      });
    }
  });

  it('MstCities menu should load MstCities page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-city');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstCity').should('exist');
    cy.url().should('match', mstCityPageUrlPattern);
  });

  describe('MstCity page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstCityPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstCity page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-city/new$'));
        cy.getEntityCreateUpdateHeading('MstCity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCityPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-cities',
          body: mstCitySample,
        }).then(({ body }) => {
          mstCity = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-cities+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-cities?page=0&size=20>; rel="last",<http://localhost/api/mst-cities?page=0&size=20>; rel="first"',
              },
              body: [mstCity],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstCityPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstCity page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstCity');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCityPageUrlPattern);
      });

      it('edit button click should load edit MstCity page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstCity');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCityPageUrlPattern);
      });

      it.skip('edit button click should load edit MstCity page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstCity');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCityPageUrlPattern);
      });

      it('last delete button click should delete instance of MstCity', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstCity').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCityPageUrlPattern);

        mstCity = undefined;
      });
    });
  });

  describe('new MstCity page', () => {
    beforeEach(() => {
      cy.visit(`${mstCityPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstCity');
    });

    it('should create an instance of MstCity', () => {
      cy.get(`[data-cy="name"]`).type('unabashedly except that');
      cy.get(`[data-cy="name"]`).should('have.value', 'unabashedly except that');

      cy.get(`[data-cy="unm49Code"]`).type('although');
      cy.get(`[data-cy="unm49Code"]`).should('have.value', 'although');

      cy.get(`[data-cy="isoAlpha2Code"]`).type('trachoma');
      cy.get(`[data-cy="isoAlpha2Code"]`).should('have.value', 'trachoma');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstCity = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstCityPageUrlPattern);
    });
  });
});
