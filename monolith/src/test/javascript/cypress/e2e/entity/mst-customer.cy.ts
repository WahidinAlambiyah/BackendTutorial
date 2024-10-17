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

describe('MstCustomer e2e test', () => {
  const mstCustomerPageUrl = '/mst-customer';
  const mstCustomerPageUrlPattern = new RegExp('/mst-customer(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstCustomerSample = { firstName: 'Jerald', lastName: 'Cormier', email: 'Brent.Dibbert@yahoo.com' };

  let mstCustomer;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-customers+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-customers').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-customers/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstCustomer) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-customers/${mstCustomer.id}`,
      }).then(() => {
        mstCustomer = undefined;
      });
    }
  });

  it('MstCustomers menu should load MstCustomers page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-customer');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstCustomer').should('exist');
    cy.url().should('match', mstCustomerPageUrlPattern);
  });

  describe('MstCustomer page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstCustomerPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstCustomer page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-customer/new$'));
        cy.getEntityCreateUpdateHeading('MstCustomer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCustomerPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-customers',
          body: mstCustomerSample,
        }).then(({ body }) => {
          mstCustomer = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-customers+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-customers?page=0&size=20>; rel="last",<http://localhost/api/mst-customers?page=0&size=20>; rel="first"',
              },
              body: [mstCustomer],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstCustomerPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstCustomer page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstCustomer');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCustomerPageUrlPattern);
      });

      it('edit button click should load edit MstCustomer page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstCustomer');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCustomerPageUrlPattern);
      });

      it.skip('edit button click should load edit MstCustomer page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstCustomer');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCustomerPageUrlPattern);
      });

      it('last delete button click should delete instance of MstCustomer', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstCustomer').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstCustomerPageUrlPattern);

        mstCustomer = undefined;
      });
    });
  });

  describe('new MstCustomer page', () => {
    beforeEach(() => {
      cy.visit(`${mstCustomerPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstCustomer');
    });

    it('should create an instance of MstCustomer', () => {
      cy.get(`[data-cy="firstName"]`).type('Jessyca');
      cy.get(`[data-cy="firstName"]`).should('have.value', 'Jessyca');

      cy.get(`[data-cy="lastName"]`).type('Abernathy');
      cy.get(`[data-cy="lastName"]`).should('have.value', 'Abernathy');

      cy.get(`[data-cy="email"]`).type('Tracy40@yahoo.com');
      cy.get(`[data-cy="email"]`).should('have.value', 'Tracy40@yahoo.com');

      cy.get(`[data-cy="phoneNumber"]`).type('reliable');
      cy.get(`[data-cy="phoneNumber"]`).should('have.value', 'reliable');

      cy.get(`[data-cy="address"]`).type('before but');
      cy.get(`[data-cy="address"]`).should('have.value', 'before but');

      cy.get(`[data-cy="loyaltyPoints"]`).type('26593');
      cy.get(`[data-cy="loyaltyPoints"]`).should('have.value', '26593');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstCustomer = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstCustomerPageUrlPattern);
    });
  });
});
