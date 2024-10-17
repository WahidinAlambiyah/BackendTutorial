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

describe('MstSubDistrict e2e test', () => {
  const mstSubDistrictPageUrl = '/mst-sub-district';
  const mstSubDistrictPageUrlPattern = new RegExp('/mst-sub-district(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstSubDistrictSample = { name: 'obedient judgementally' };

  let mstSubDistrict;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-sub-districts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-sub-districts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-sub-districts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstSubDistrict) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-sub-districts/${mstSubDistrict.id}`,
      }).then(() => {
        mstSubDistrict = undefined;
      });
    }
  });

  it('MstSubDistricts menu should load MstSubDistricts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-sub-district');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstSubDistrict').should('exist');
    cy.url().should('match', mstSubDistrictPageUrlPattern);
  });

  describe('MstSubDistrict page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstSubDistrictPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstSubDistrict page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-sub-district/new$'));
        cy.getEntityCreateUpdateHeading('MstSubDistrict');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSubDistrictPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-sub-districts',
          body: mstSubDistrictSample,
        }).then(({ body }) => {
          mstSubDistrict = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-sub-districts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-sub-districts?page=0&size=20>; rel="last",<http://localhost/api/mst-sub-districts?page=0&size=20>; rel="first"',
              },
              body: [mstSubDistrict],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstSubDistrictPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstSubDistrict page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstSubDistrict');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSubDistrictPageUrlPattern);
      });

      it('edit button click should load edit MstSubDistrict page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstSubDistrict');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSubDistrictPageUrlPattern);
      });

      it.skip('edit button click should load edit MstSubDistrict page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstSubDistrict');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSubDistrictPageUrlPattern);
      });

      it('last delete button click should delete instance of MstSubDistrict', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstSubDistrict').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstSubDistrictPageUrlPattern);

        mstSubDistrict = undefined;
      });
    });
  });

  describe('new MstSubDistrict page', () => {
    beforeEach(() => {
      cy.visit(`${mstSubDistrictPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstSubDistrict');
    });

    it('should create an instance of MstSubDistrict', () => {
      cy.get(`[data-cy="name"]`).type('actually pupil');
      cy.get(`[data-cy="name"]`).should('have.value', 'actually pupil');

      cy.get(`[data-cy="unm49Code"]`).type('so');
      cy.get(`[data-cy="unm49Code"]`).should('have.value', 'so');

      cy.get(`[data-cy="isoAlpha2Code"]`).type('whose into');
      cy.get(`[data-cy="isoAlpha2Code"]`).should('have.value', 'whose into');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstSubDistrict = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstSubDistrictPageUrlPattern);
    });
  });
});
