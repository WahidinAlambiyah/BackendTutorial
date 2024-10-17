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

describe('MstLoyaltyProgram e2e test', () => {
  const mstLoyaltyProgramPageUrl = '/mst-loyalty-program';
  const mstLoyaltyProgramPageUrlPattern = new RegExp('/mst-loyalty-program(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstLoyaltyProgramSample = { pointsEarned: 848 };

  let mstLoyaltyProgram;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-loyalty-programs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-loyalty-programs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-loyalty-programs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstLoyaltyProgram) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-loyalty-programs/${mstLoyaltyProgram.id}`,
      }).then(() => {
        mstLoyaltyProgram = undefined;
      });
    }
  });

  it('MstLoyaltyPrograms menu should load MstLoyaltyPrograms page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-loyalty-program');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstLoyaltyProgram').should('exist');
    cy.url().should('match', mstLoyaltyProgramPageUrlPattern);
  });

  describe('MstLoyaltyProgram page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstLoyaltyProgramPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstLoyaltyProgram page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-loyalty-program/new$'));
        cy.getEntityCreateUpdateHeading('MstLoyaltyProgram');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstLoyaltyProgramPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-loyalty-programs',
          body: mstLoyaltyProgramSample,
        }).then(({ body }) => {
          mstLoyaltyProgram = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-loyalty-programs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-loyalty-programs?page=0&size=20>; rel="last",<http://localhost/api/mst-loyalty-programs?page=0&size=20>; rel="first"',
              },
              body: [mstLoyaltyProgram],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstLoyaltyProgramPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstLoyaltyProgram page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstLoyaltyProgram');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstLoyaltyProgramPageUrlPattern);
      });

      it('edit button click should load edit MstLoyaltyProgram page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstLoyaltyProgram');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstLoyaltyProgramPageUrlPattern);
      });

      it.skip('edit button click should load edit MstLoyaltyProgram page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstLoyaltyProgram');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstLoyaltyProgramPageUrlPattern);
      });

      it('last delete button click should delete instance of MstLoyaltyProgram', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstLoyaltyProgram').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstLoyaltyProgramPageUrlPattern);

        mstLoyaltyProgram = undefined;
      });
    });
  });

  describe('new MstLoyaltyProgram page', () => {
    beforeEach(() => {
      cy.visit(`${mstLoyaltyProgramPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstLoyaltyProgram');
    });

    it('should create an instance of MstLoyaltyProgram', () => {
      cy.get(`[data-cy="pointsEarned"]`).type('23578');
      cy.get(`[data-cy="pointsEarned"]`).should('have.value', '23578');

      cy.get(`[data-cy="membershipTier"]`).type('exhaust clamour anise');
      cy.get(`[data-cy="membershipTier"]`).should('have.value', 'exhaust clamour anise');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstLoyaltyProgram = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstLoyaltyProgramPageUrlPattern);
    });
  });
});
