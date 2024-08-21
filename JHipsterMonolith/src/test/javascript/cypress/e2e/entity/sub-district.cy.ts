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

describe('SubDistrict e2e test', () => {
  const subDistrictPageUrl = '/sub-district';
  const subDistrictPageUrlPattern = new RegExp('/sub-district(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const subDistrictSample = { name: 'slit exhaustion unexpectedly', code: 'nor readily' };

  let subDistrict;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/sub-districts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/sub-districts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/sub-districts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (subDistrict) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/sub-districts/${subDistrict.id}`,
      }).then(() => {
        subDistrict = undefined;
      });
    }
  });

  it('SubDistricts menu should load SubDistricts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('sub-district');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('SubDistrict').should('exist');
    cy.url().should('match', subDistrictPageUrlPattern);
  });

  describe('SubDistrict page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(subDistrictPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create SubDistrict page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/sub-district/new$'));
        cy.getEntityCreateUpdateHeading('SubDistrict');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subDistrictPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/sub-districts',
          body: subDistrictSample,
        }).then(({ body }) => {
          subDistrict = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/sub-districts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/sub-districts?page=0&size=20>; rel="last",<http://localhost/api/sub-districts?page=0&size=20>; rel="first"',
              },
              body: [subDistrict],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(subDistrictPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details SubDistrict page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('subDistrict');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subDistrictPageUrlPattern);
      });

      it('edit button click should load edit SubDistrict page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SubDistrict');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subDistrictPageUrlPattern);
      });

      it('edit button click should load edit SubDistrict page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('SubDistrict');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subDistrictPageUrlPattern);
      });

      it('last delete button click should delete instance of SubDistrict', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('subDistrict').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', subDistrictPageUrlPattern);

        subDistrict = undefined;
      });
    });
  });

  describe('new SubDistrict page', () => {
    beforeEach(() => {
      cy.visit(`${subDistrictPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('SubDistrict');
    });

    it('should create an instance of SubDistrict', () => {
      cy.get(`[data-cy="name"]`).type('hiking jealously');
      cy.get(`[data-cy="name"]`).should('have.value', 'hiking jealously');

      cy.get(`[data-cy="code"]`).type('accidentally staircase');
      cy.get(`[data-cy="code"]`).should('have.value', 'accidentally staircase');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        subDistrict = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', subDistrictPageUrlPattern);
    });
  });
});
