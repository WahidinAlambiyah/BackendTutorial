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

describe('TrxStockAlert e2e test', () => {
  const trxStockAlertPageUrl = '/trx-stock-alert';
  const trxStockAlertPageUrlPattern = new RegExp('/trx-stock-alert(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxStockAlertSample = { alertThreshold: 27091, currentStock: 13374 };

  let trxStockAlert;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-stock-alerts+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-stock-alerts').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-stock-alerts/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxStockAlert) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-stock-alerts/${trxStockAlert.id}`,
      }).then(() => {
        trxStockAlert = undefined;
      });
    }
  });

  it('TrxStockAlerts menu should load TrxStockAlerts page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-stock-alert');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxStockAlert').should('exist');
    cy.url().should('match', trxStockAlertPageUrlPattern);
  });

  describe('TrxStockAlert page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxStockAlertPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxStockAlert page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-stock-alert/new$'));
        cy.getEntityCreateUpdateHeading('TrxStockAlert');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxStockAlertPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-stock-alerts',
          body: trxStockAlertSample,
        }).then(({ body }) => {
          trxStockAlert = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-stock-alerts+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-stock-alerts?page=0&size=20>; rel="last",<http://localhost/api/trx-stock-alerts?page=0&size=20>; rel="first"',
              },
              body: [trxStockAlert],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxStockAlertPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxStockAlert page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxStockAlert');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxStockAlertPageUrlPattern);
      });

      it('edit button click should load edit TrxStockAlert page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxStockAlert');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxStockAlertPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxStockAlert page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxStockAlert');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxStockAlertPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxStockAlert', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxStockAlert').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxStockAlertPageUrlPattern);

        trxStockAlert = undefined;
      });
    });
  });

  describe('new TrxStockAlert page', () => {
    beforeEach(() => {
      cy.visit(`${trxStockAlertPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxStockAlert');
    });

    it('should create an instance of TrxStockAlert', () => {
      cy.get(`[data-cy="alertThreshold"]`).type('4007');
      cy.get(`[data-cy="alertThreshold"]`).should('have.value', '4007');

      cy.get(`[data-cy="currentStock"]`).type('4128');
      cy.get(`[data-cy="currentStock"]`).should('have.value', '4128');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxStockAlert = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxStockAlertPageUrlPattern);
    });
  });
});
