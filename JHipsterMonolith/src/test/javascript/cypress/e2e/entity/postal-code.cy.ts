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

describe('PostalCode e2e test', () => {
  const postalCodePageUrl = '/postal-code';
  const postalCodePageUrlPattern = new RegExp('/postal-code(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const postalCodeSample = { code: 'mortally rough aboard' };

  let postalCode;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/postal-codes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/postal-codes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/postal-codes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (postalCode) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/postal-codes/${postalCode.id}`,
      }).then(() => {
        postalCode = undefined;
      });
    }
  });

  it('PostalCodes menu should load PostalCodes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('postal-code');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('PostalCode').should('exist');
    cy.url().should('match', postalCodePageUrlPattern);
  });

  describe('PostalCode page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(postalCodePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create PostalCode page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/postal-code/new$'));
        cy.getEntityCreateUpdateHeading('PostalCode');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', postalCodePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/postal-codes',
          body: postalCodeSample,
        }).then(({ body }) => {
          postalCode = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/postal-codes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/postal-codes?page=0&size=20>; rel="last",<http://localhost/api/postal-codes?page=0&size=20>; rel="first"',
              },
              body: [postalCode],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(postalCodePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details PostalCode page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('postalCode');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', postalCodePageUrlPattern);
      });

      it('edit button click should load edit PostalCode page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PostalCode');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', postalCodePageUrlPattern);
      });

      it.skip('edit button click should load edit PostalCode page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('PostalCode');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', postalCodePageUrlPattern);
      });

      it('last delete button click should delete instance of PostalCode', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('postalCode').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', postalCodePageUrlPattern);

        postalCode = undefined;
      });
    });
  });

  describe('new PostalCode page', () => {
    beforeEach(() => {
      cy.visit(`${postalCodePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('PostalCode');
    });

    it('should create an instance of PostalCode', () => {
      cy.get(`[data-cy="code"]`).type('concrete inwardly');
      cy.get(`[data-cy="code"]`).should('have.value', 'concrete inwardly');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        postalCode = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', postalCodePageUrlPattern);
    });
  });
});
