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

describe('MstPostalCode e2e test', () => {
  const mstPostalCodePageUrl = '/mst-postal-code';
  const mstPostalCodePageUrlPattern = new RegExp('/mst-postal-code(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstPostalCodeSample = { code: 'defiantly notwithstanding' };

  let mstPostalCode;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-postal-codes+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-postal-codes').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-postal-codes/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstPostalCode) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-postal-codes/${mstPostalCode.id}`,
      }).then(() => {
        mstPostalCode = undefined;
      });
    }
  });

  it('MstPostalCodes menu should load MstPostalCodes page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-postal-code');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstPostalCode').should('exist');
    cy.url().should('match', mstPostalCodePageUrlPattern);
  });

  describe('MstPostalCode page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstPostalCodePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstPostalCode page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-postal-code/new$'));
        cy.getEntityCreateUpdateHeading('MstPostalCode');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstPostalCodePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-postal-codes',
          body: mstPostalCodeSample,
        }).then(({ body }) => {
          mstPostalCode = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-postal-codes+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-postal-codes?page=0&size=20>; rel="last",<http://localhost/api/mst-postal-codes?page=0&size=20>; rel="first"',
              },
              body: [mstPostalCode],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstPostalCodePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstPostalCode page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstPostalCode');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstPostalCodePageUrlPattern);
      });

      it('edit button click should load edit MstPostalCode page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstPostalCode');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstPostalCodePageUrlPattern);
      });

      it.skip('edit button click should load edit MstPostalCode page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstPostalCode');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstPostalCodePageUrlPattern);
      });

      it('last delete button click should delete instance of MstPostalCode', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstPostalCode').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstPostalCodePageUrlPattern);

        mstPostalCode = undefined;
      });
    });
  });

  describe('new MstPostalCode page', () => {
    beforeEach(() => {
      cy.visit(`${mstPostalCodePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstPostalCode');
    });

    it('should create an instance of MstPostalCode', () => {
      cy.get(`[data-cy="code"]`).type('tomorrow ouch');
      cy.get(`[data-cy="code"]`).should('have.value', 'tomorrow ouch');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstPostalCode = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstPostalCodePageUrlPattern);
    });
  });
});
