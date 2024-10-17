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

describe('MstDepartment e2e test', () => {
  const mstDepartmentPageUrl = '/mst-department';
  const mstDepartmentPageUrlPattern = new RegExp('/mst-department(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstDepartmentSample = { departmentName: 'before' };

  let mstDepartment;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-departments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-departments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-departments/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstDepartment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-departments/${mstDepartment.id}`,
      }).then(() => {
        mstDepartment = undefined;
      });
    }
  });

  it('MstDepartments menu should load MstDepartments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-department');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstDepartment').should('exist');
    cy.url().should('match', mstDepartmentPageUrlPattern);
  });

  describe('MstDepartment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstDepartmentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstDepartment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-department/new$'));
        cy.getEntityCreateUpdateHeading('MstDepartment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDepartmentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-departments',
          body: mstDepartmentSample,
        }).then(({ body }) => {
          mstDepartment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-departments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-departments?page=0&size=20>; rel="last",<http://localhost/api/mst-departments?page=0&size=20>; rel="first"',
              },
              body: [mstDepartment],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstDepartmentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstDepartment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstDepartment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDepartmentPageUrlPattern);
      });

      it('edit button click should load edit MstDepartment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstDepartment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDepartmentPageUrlPattern);
      });

      it.skip('edit button click should load edit MstDepartment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstDepartment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDepartmentPageUrlPattern);
      });

      it('last delete button click should delete instance of MstDepartment', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstDepartment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstDepartmentPageUrlPattern);

        mstDepartment = undefined;
      });
    });
  });

  describe('new MstDepartment page', () => {
    beforeEach(() => {
      cy.visit(`${mstDepartmentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstDepartment');
    });

    it('should create an instance of MstDepartment', () => {
      cy.get(`[data-cy="departmentName"]`).type('behind shack');
      cy.get(`[data-cy="departmentName"]`).should('have.value', 'behind shack');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstDepartment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstDepartmentPageUrlPattern);
    });
  });
});
