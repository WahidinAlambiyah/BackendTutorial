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

describe('MstTask e2e test', () => {
  const mstTaskPageUrl = '/mst-task';
  const mstTaskPageUrlPattern = new RegExp('/mst-task(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstTaskSample = { title: 'whose' };

  let mstTask;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-tasks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-tasks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-tasks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstTask) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-tasks/${mstTask.id}`,
      }).then(() => {
        mstTask = undefined;
      });
    }
  });

  it('MstTasks menu should load MstTasks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-task');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstTask').should('exist');
    cy.url().should('match', mstTaskPageUrlPattern);
  });

  describe('MstTask page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstTaskPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstTask page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-task/new$'));
        cy.getEntityCreateUpdateHeading('MstTask');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstTaskPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-tasks',
          body: mstTaskSample,
        }).then(({ body }) => {
          mstTask = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-tasks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-tasks?page=0&size=20>; rel="last",<http://localhost/api/mst-tasks?page=0&size=20>; rel="first"',
              },
              body: [mstTask],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstTaskPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstTask page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstTask');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstTaskPageUrlPattern);
      });

      it('edit button click should load edit MstTask page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstTask');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstTaskPageUrlPattern);
      });

      it.skip('edit button click should load edit MstTask page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstTask');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstTaskPageUrlPattern);
      });

      it('last delete button click should delete instance of MstTask', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstTask').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstTaskPageUrlPattern);

        mstTask = undefined;
      });
    });
  });

  describe('new MstTask page', () => {
    beforeEach(() => {
      cy.visit(`${mstTaskPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstTask');
    });

    it('should create an instance of MstTask', () => {
      cy.get(`[data-cy="title"]`).type('guess lest');
      cy.get(`[data-cy="title"]`).should('have.value', 'guess lest');

      cy.get(`[data-cy="description"]`).type('if');
      cy.get(`[data-cy="description"]`).should('have.value', 'if');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstTask = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstTaskPageUrlPattern);
    });
  });
});
