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

describe('Stock e2e test', () => {
  const stockPageUrl = '/stock';
  const stockPageUrlPattern = new RegExp('/stock(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const stockSample = { quantityAvailable: 19343 };

  let stock;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/stocks+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/stocks').as('postEntityRequest');
    cy.intercept('DELETE', '/api/stocks/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (stock) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/stocks/${stock.id}`,
      }).then(() => {
        stock = undefined;
      });
    }
  });

  it('Stocks menu should load Stocks page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('stock');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Stock').should('exist');
    cy.url().should('match', stockPageUrlPattern);
  });

  describe('Stock page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(stockPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Stock page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/stock/new$'));
        cy.getEntityCreateUpdateHeading('Stock');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/stocks',
          body: stockSample,
        }).then(({ body }) => {
          stock = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/stocks+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/stocks?page=0&size=20>; rel="last",<http://localhost/api/stocks?page=0&size=20>; rel="first"',
              },
              body: [stock],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(stockPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Stock page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('stock');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);
      });

      it('edit button click should load edit Stock page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Stock');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);
      });

      it.skip('edit button click should load edit Stock page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Stock');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);
      });

      it('last delete button click should delete instance of Stock', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('stock').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', stockPageUrlPattern);

        stock = undefined;
      });
    });
  });

  describe('new Stock page', () => {
    beforeEach(() => {
      cy.visit(`${stockPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Stock');
    });

    it('should create an instance of Stock', () => {
      cy.get(`[data-cy="quantityAvailable"]`).type('25526');
      cy.get(`[data-cy="quantityAvailable"]`).should('have.value', '25526');

      cy.get(`[data-cy="reorderLevel"]`).type('29677');
      cy.get(`[data-cy="reorderLevel"]`).should('have.value', '29677');

      cy.get(`[data-cy="expiryDate"]`).type('2024-10-16T10:02');
      cy.get(`[data-cy="expiryDate"]`).blur();
      cy.get(`[data-cy="expiryDate"]`).should('have.value', '2024-10-16T10:02');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        stock = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', stockPageUrlPattern);
    });
  });
});
