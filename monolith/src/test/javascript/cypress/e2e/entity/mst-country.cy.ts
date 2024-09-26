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

describe('MstCountry e2e test', () => {
  const mstCountryPageUrl = '/mst-country';
  const mstCountryPageUrlPattern = new RegExp('/mst-country(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstCountrySample = { name: 'puppet imperfect dependent' };

  let mstCountry;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-countries+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-countries').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-countries/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstCountry) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-countries/${mstCountry.id}`,
      }).then(() => {
        mstCountry = undefined;
      });
    }
  });

  it('MstCountries menu should load MstCountries page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-country');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstCountry').should('exist');
    cy.url().should('match', mstCountryPageUrlPattern);
  });

  describe('MstCountry page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstCountryPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstCountry page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-country/new$'));
        cy.getEntityCreateUpdateHeading('MstCountry');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCountryPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-countries',
          body: mstCountrySample,
        }).then(({ body }) => {
          mstCountry = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-countries+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-countries?page=0&size=20>; rel="last",<http://localhost/api/mst-countries?page=0&size=20>; rel="first"',
              },
              body: [mstCountry],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstCountryPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstCountry page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstCountry');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCountryPageUrlPattern);
      });

      it('edit button click should load edit MstCountry page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstCountry');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCountryPageUrlPattern);
      });

      it.skip('edit button click should load edit MstCountry page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstCountry');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCountryPageUrlPattern);
      });

      it('last delete button click should delete instance of MstCountry', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstCountry').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCountryPageUrlPattern);

        mstCountry = undefined;
      });
    });
  });

  describe('new MstCountry page', () => {
    beforeEach(() => {
      cy.visit(`${mstCountryPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstCountry');
    });

    it('should create an instance of MstCountry', () => {
      cy.get(`[data-cy="name"]`).type('unsteady muck');
      cy.get(`[data-cy="name"]`).should('have.value', 'unsteady muck');

      cy.get(`[data-cy="unm49Code"]`).type('gear quietly where');
      cy.get(`[data-cy="unm49Code"]`).should('have.value', 'gear quietly where');

      cy.get(`[data-cy="isoAlpha2Code"]`).type('the ick');
      cy.get(`[data-cy="isoAlpha2Code"]`).should('have.value', 'the ick');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstCountry = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstCountryPageUrlPattern);
    });
  });
});
