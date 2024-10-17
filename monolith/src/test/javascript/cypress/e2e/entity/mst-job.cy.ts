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

describe('MstJob e2e test', () => {
  const mstJobPageUrl = '/mst-job';
  const mstJobPageUrlPattern = new RegExp('/mst-job(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstJobSample = { jobTitle: 'Legacy Integration Assistant' };

  let mstJob;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-jobs+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-jobs').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-jobs/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstJob) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-jobs/${mstJob.id}`,
      }).then(() => {
        mstJob = undefined;
      });
    }
  });

  it('MstJobs menu should load MstJobs page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-job');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstJob').should('exist');
    cy.url().should('match', mstJobPageUrlPattern);
  });

  describe('MstJob page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstJobPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstJob page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-job/new$'));
        cy.getEntityCreateUpdateHeading('MstJob');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstJobPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-jobs',
          body: mstJobSample,
        }).then(({ body }) => {
          mstJob = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-jobs+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-jobs?page=0&size=20>; rel="last",<http://localhost/api/mst-jobs?page=0&size=20>; rel="first"',
              },
              body: [mstJob],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstJobPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstJob page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstJob');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstJobPageUrlPattern);
      });

      it('edit button click should load edit MstJob page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstJob');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstJobPageUrlPattern);
      });

      it.skip('edit button click should load edit MstJob page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstJob');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstJobPageUrlPattern);
      });

      it('last delete button click should delete instance of MstJob', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstJob').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstJobPageUrlPattern);

        mstJob = undefined;
      });
    });
  });

  describe('new MstJob page', () => {
    beforeEach(() => {
      cy.visit(`${mstJobPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstJob');
    });

    it('should create an instance of MstJob', () => {
      cy.get(`[data-cy="jobTitle"]`).type('Future Infrastructure Specialist');
      cy.get(`[data-cy="jobTitle"]`).should('have.value', 'Future Infrastructure Specialist');

      cy.get(`[data-cy="minSalary"]`).type('27984');
      cy.get(`[data-cy="minSalary"]`).should('have.value', '27984');

      cy.get(`[data-cy="maxSalary"]`).type('22921');
      cy.get(`[data-cy="maxSalary"]`).should('have.value', '22921');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstJob = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstJobPageUrlPattern);
    });
  });
});
