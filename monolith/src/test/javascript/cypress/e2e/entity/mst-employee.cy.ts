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

describe('MstEmployee e2e test', () => {
  const mstEmployeePageUrl = '/mst-employee';
  const mstEmployeePageUrlPattern = new RegExp('/mst-employee(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstEmployeeSample = { firstName: 'Aurore' };

  let mstEmployee;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-employees+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-employees').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-employees/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstEmployee) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-employees/${mstEmployee.id}`,
      }).then(() => {
        mstEmployee = undefined;
      });
    }
  });

  it('MstEmployees menu should load MstEmployees page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-employee');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstEmployee').should('exist');
    cy.url().should('match', mstEmployeePageUrlPattern);
  });

  describe('MstEmployee page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstEmployeePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstEmployee page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-employee/new$'));
        cy.getEntityCreateUpdateHeading('MstEmployee');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstEmployeePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-employees',
          body: mstEmployeeSample,
        }).then(({ body }) => {
          mstEmployee = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-employees+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-employees?page=0&size=20>; rel="last",<http://localhost/api/mst-employees?page=0&size=20>; rel="first"',
              },
              body: [mstEmployee],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstEmployeePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstEmployee page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstEmployee');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstEmployeePageUrlPattern);
      });

      it('edit button click should load edit MstEmployee page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstEmployee');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstEmployeePageUrlPattern);
      });

      it.skip('edit button click should load edit MstEmployee page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstEmployee');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstEmployeePageUrlPattern);
      });

      it('last delete button click should delete instance of MstEmployee', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstEmployee').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstEmployeePageUrlPattern);

        mstEmployee = undefined;
      });
    });
  });

  describe('new MstEmployee page', () => {
    beforeEach(() => {
      cy.visit(`${mstEmployeePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstEmployee');
    });

    it('should create an instance of MstEmployee', () => {
      cy.get(`[data-cy="firstName"]`).type('Mateo');
      cy.get(`[data-cy="firstName"]`).should('have.value', 'Mateo');

      cy.get(`[data-cy="lastName"]`).type('Hagenes');
      cy.get(`[data-cy="lastName"]`).should('have.value', 'Hagenes');

      cy.get(`[data-cy="email"]`).type('Alex_McCullough63@gmail.com');
      cy.get(`[data-cy="email"]`).should('have.value', 'Alex_McCullough63@gmail.com');

      cy.get(`[data-cy="phoneNumber"]`).type('setting consequently mmm');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', 'setting consequently mmm');

      cy.get(`[data-cy="hireDate"]`).type('2024-09-23T18:23');
      cy.get(`[data-cy="hireDate"]`).blur();
      cy.get(`[data-cy="hireDate"]`).should('have.value', '2024-09-23T18:23');

      cy.get(`[data-cy="salary"]`).type('5775');
      cy.get(`[data-cy="salary"]`).should('have.value', '5775');

      cy.get(`[data-cy="commissionPct"]`).type('4630');
      cy.get(`[data-cy="commissionPct"]`).should('have.value', '4630');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstEmployee = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstEmployeePageUrlPattern);
    });
  });
});
