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

describe('TrxEvent e2e test', () => {
  const trxEventPageUrl = '/trx-event';
  const trxEventPageUrlPattern = new RegExp('/trx-event(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxEventSample = { title: 'pace sharpen', date: '2024-09-24T06:18:17.298Z' };

  let trxEvent;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-events+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-events').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-events/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxEvent) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-events/${trxEvent.id}`,
      }).then(() => {
        trxEvent = undefined;
      });
    }
  });

  it('TrxEvents menu should load TrxEvents page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-event');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxEvent').should('exist');
    cy.url().should('match', trxEventPageUrlPattern);
  });

  describe('TrxEvent page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxEventPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxEvent page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-event/new$'));
        cy.getEntityCreateUpdateHeading('TrxEvent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxEventPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-events',
          body: trxEventSample,
        }).then(({ body }) => {
          trxEvent = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-events+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/trx-events?page=0&size=20>; rel="last",<http://localhost/api/trx-events?page=0&size=20>; rel="first"',
              },
              body: [trxEvent],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxEventPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxEvent page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxEvent');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxEventPageUrlPattern);
      });

      it('edit button click should load edit TrxEvent page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxEvent');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxEventPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxEvent page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxEvent');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxEventPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxEvent', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxEvent').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxEventPageUrlPattern);

        trxEvent = undefined;
      });
    });
  });

  describe('new TrxEvent page', () => {
    beforeEach(() => {
      cy.visit(`${trxEventPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxEvent');
    });

    it('should create an instance of TrxEvent', () => {
      cy.get(`[data-cy="title"]`).type('officially savor among');
      cy.get(`[data-cy="title"]`).should('have.value', 'officially savor among');

      cy.get(`[data-cy="description"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="description"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="date"]`).type('2024-09-24T06:33');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2024-09-24T06:33');

      cy.get(`[data-cy="location"]`).type('yuck boo');
      cy.get(`[data-cy="location"]`).should('have.value', 'yuck boo');

      cy.get(`[data-cy="capacity"]`).type('24352');
      cy.get(`[data-cy="capacity"]`).should('have.value', '24352');

      cy.get(`[data-cy="price"]`).type('29626.22');
      cy.get(`[data-cy="price"]`).should('have.value', '29626.22');

      cy.get(`[data-cy="status"]`).select('CANCELLED');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxEvent = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxEventPageUrlPattern);
    });
  });
});
