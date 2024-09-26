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

describe('TrxTestimonial e2e test', () => {
  const trxTestimonialPageUrl = '/trx-testimonial';
  const trxTestimonialPageUrlPattern = new RegExp('/trx-testimonial(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const trxTestimonialSample = {
    name: 'pfft flintlock',
    feedback: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
    rating: 3,
    date: '2024-09-23T17:37:47.907Z',
  };

  let trxTestimonial;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/trx-testimonials+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/trx-testimonials').as('postEntityRequest');
    cy.intercept('DELETE', '/api/trx-testimonials/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (trxTestimonial) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/trx-testimonials/${trxTestimonial.id}`,
      }).then(() => {
        trxTestimonial = undefined;
      });
    }
  });

  it('TrxTestimonials menu should load TrxTestimonials page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('trx-testimonial');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('TrxTestimonial').should('exist');
    cy.url().should('match', trxTestimonialPageUrlPattern);
  });

  describe('TrxTestimonial page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(trxTestimonialPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create TrxTestimonial page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/trx-testimonial/new$'));
        cy.getEntityCreateUpdateHeading('TrxTestimonial');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTestimonialPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/trx-testimonials',
          body: trxTestimonialSample,
        }).then(({ body }) => {
          trxTestimonial = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/trx-testimonials+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [trxTestimonial],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(trxTestimonialPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details TrxTestimonial page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('trxTestimonial');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTestimonialPageUrlPattern);
      });

      it('edit button click should load edit TrxTestimonial page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxTestimonial');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTestimonialPageUrlPattern);
      });

      it.skip('edit button click should load edit TrxTestimonial page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('TrxTestimonial');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTestimonialPageUrlPattern);
      });

      it('last delete button click should delete instance of TrxTestimonial', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('trxTestimonial').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', trxTestimonialPageUrlPattern);

        trxTestimonial = undefined;
      });
    });
  });

  describe('new TrxTestimonial page', () => {
    beforeEach(() => {
      cy.visit(`${trxTestimonialPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('TrxTestimonial');
    });

    it('should create an instance of TrxTestimonial', () => {
      cy.get(`[data-cy="name"]`).type('for nor');
      cy.get(`[data-cy="name"]`).should('have.value', 'for nor');

      cy.get(`[data-cy="feedback"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="feedback"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="rating"]`).type('4');
      cy.get(`[data-cy="rating"]`).should('have.value', '4');

      cy.get(`[data-cy="date"]`).type('2024-09-23T19:42');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2024-09-23T19:42');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        trxTestimonial = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', trxTestimonialPageUrlPattern);
    });
  });
});
