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

describe('TrxNotification e2e test', () => {
  const trxNotificationPageUrl = '/trx-notification';
  const trxNotificationPageUrlPattern = new RegExp('/trx-notification(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxNotificationSample = {
    recipient: 'garrotte',
    messageType: 'kindly as',
    content: 'modem wonderful',
    sentAt: '2024-10-16T12:10:24.591Z',
  };

  let trxNotification;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-notifications+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-notifications').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-notifications/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxNotification) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-notifications/${trxNotification.id}`,
      }).then(() => {
        trxNotification = undefined;
      });
    }
  });

  it('TrxNotifications menu should load TrxNotifications page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-notification');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxNotification').should('exist');
    cy.url().should('match', trxNotificationPageUrlPattern);
  });

  describe('TrxNotification page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxNotificationPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxNotification page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-notification/new$'));
        cy.getEntityCreateUpdateHeading('TrxNotification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxNotificationPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-notifications',
          body: trxNotificationSample,
        }).then(({ body }) => {
          trxNotification = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-notifications+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-notifications?page=0&size=20>; rel="last",<http://localhost/api/trx-notifications?page=0&size=20>; rel="first"',
              },
              body: [trxNotification],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxNotificationPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxNotification page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxNotification');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxNotificationPageUrlPattern);
      });

      it('edit button click should load edit TrxNotification page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxNotification');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxNotificationPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxNotification page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxNotification');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxNotificationPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxNotification', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxNotification').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxNotificationPageUrlPattern);

        trxNotification = undefined;
      });
    });
  });

  describe('new TrxNotification page', () => {
    beforeEach(() => {
      cy.visit(`${trxNotificationPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxNotification');
    });

    it('should create an instance of TrxNotification', () => {
      cy.get(`[data-cy="recipient"]`).type('tightly learn past');
      cy.get(`[data-cy="recipient"]`).should('have.value', 'tightly learn past');

      cy.get(`[data-cy="messageType"]`).type('very boo');
      cy.get(`[data-cy="messageType"]`).should('have.value', 'very boo');

      cy.get(`[data-cy="content"]`).type('pike');
      cy.get(`[data-cy="content"]`).should('have.value', 'pike');

      cy.get(`[data-cy="sentAt"]`).type('2024-10-17T00:04');
      cy.get(`[data-cy="sentAt"]`).blur();
      cy.get(`[data-cy="sentAt"]`).should('have.value', '2024-10-17T00:04');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxNotification = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxNotificationPageUrlPattern);
    });
  });
});
