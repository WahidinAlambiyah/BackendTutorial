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

describe('MstDistrict e2e test', () => {
  const mstDistrictPageUrl = '/mst-district';
  const mstDistrictPageUrlPattern = new RegExp('/mst-district(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstDistrictSample = { name: 'elderly opposite' };

  let mstDistrict;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-districts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-districts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-districts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstDistrict) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-districts/${mstDistrict.id}`,
      }).then(() => {
        mstDistrict = undefined;
      });
    }
  });

  it('MstDistricts menu should load MstDistricts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-district');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstDistrict').should('exist');
    cy.url().should('match', mstDistrictPageUrlPattern);
  });

  describe('MstDistrict page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstDistrictPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstDistrict page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-district/new$'));
        cy.getEntityCreateUpdateHeading('MstDistrict');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDistrictPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-districts',
          body: mstDistrictSample,
        }).then(({ body }) => {
          mstDistrict = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-districts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-districts?page=0&size=20>; rel="last",<http://localhost/api/mst-districts?page=0&size=20>; rel="first"',
              },
              body: [mstDistrict],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstDistrictPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstDistrict page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstDistrict');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDistrictPageUrlPattern);
      });

      it('edit button click should load edit MstDistrict page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstDistrict');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDistrictPageUrlPattern);
      });

      it.skip('edit button click should load edit MstDistrict page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstDistrict');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDistrictPageUrlPattern);
      });

      it('last delete button click should delete instance of MstDistrict', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstDistrict').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDistrictPageUrlPattern);

        mstDistrict = undefined;
      });
    });
  });

  describe('new MstDistrict page', () => {
    beforeEach(() => {
      cy.visit(`${mstDistrictPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstDistrict');
    });

    it('should create an instance of MstDistrict', () => {
      cy.get(`[data-cy="name"]`).type('depopulate past');
      cy.get(`[data-cy="name"]`).should('have.value', 'depopulate past');

      cy.get(`[data-cy="unm49Code"]`).type('honeybee');
      cy.get(`[data-cy="unm49Code"]`).should('have.value', 'honeybee');

      cy.get(`[data-cy="isoAlpha2Code"]`).type('remote for coolly');
      cy.get(`[data-cy="isoAlpha2Code"]`).should('have.value', 'remote for coolly');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstDistrict = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstDistrictPageUrlPattern);
    });
  });
});
