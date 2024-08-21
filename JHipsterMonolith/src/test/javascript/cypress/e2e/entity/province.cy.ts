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

describe('Province e2e test', () => {
  const provincePageUrl = '/province';
  const provincePageUrlPattern = new RegExp('/province(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const provinceSample = { name: 'alongside', code: 'puzzle' };

  let province;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/provinces+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/provinces').as('postEntityRequest');
    cy.intercept('DELETE', '/api/provinces/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (province) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/provinces/${province.id}`,
      }).then(() => {
        province = undefined;
      });
    }
  });

  it('Provinces menu should load Provinces page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('province');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Province').should('exist');
    cy.url().should('match', provincePageUrlPattern);
  });

  describe('Province page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(provincePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Province page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/province/new$'));
        cy.getEntityCreateUpdateHeading('Province');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', provincePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/provinces',
          body: provinceSample,
        }).then(({ body }) => {
          province = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/provinces+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/provinces?page=0&size=20>; rel="last",<http://localhost/api/provinces?page=0&size=20>; rel="first"',
              },
              body: [province],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(provincePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Province page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('province');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', provincePageUrlPattern);
      });

      it('edit button click should load edit Province page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Province');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', provincePageUrlPattern);
      });

      it('edit button click should load edit Province page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Province');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', provincePageUrlPattern);
      });

      it('last delete button click should delete instance of Province', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('province').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', provincePageUrlPattern);

        province = undefined;
      });
    });
  });

  describe('new Province page', () => {
    beforeEach(() => {
      cy.visit(`${provincePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Province');
    });

    it('should create an instance of Province', () => {
      cy.get(`[data-cy="name"]`).type('wrongly proliferate');
      cy.get(`[data-cy="name"]`).should('have.value', 'wrongly proliferate');

      cy.get(`[data-cy="code"]`).type('boatyard realign hearty');
      cy.get(`[data-cy="code"]`).should('have.value', 'boatyard realign hearty');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        province = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', provincePageUrlPattern);
    });
  });
});
