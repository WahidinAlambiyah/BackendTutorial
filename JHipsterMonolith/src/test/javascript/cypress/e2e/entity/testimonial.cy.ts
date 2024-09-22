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

describe('Testimonial e2e test', () => {
  const testimonialPageUrl = '/testimonial';
  const testimonialPageUrlPattern = new RegExp('/testimonial(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const testimonialSample = {
    name: 'defiantly gift',
    feedback: 'Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci50eHQ=',
    rating: 1,
    date: '2024-09-14T00:57:46.597Z',
  };

  let testimonial;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/testimonials+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/testimonials').as('postEntityRequest');
    cy.intercept('DELETE', '/api/testimonials/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (testimonial) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/testimonials/${testimonial.id}`,
      }).then(() => {
        testimonial = undefined;
      });
    }
  });

  it('Testimonials menu should load Testimonials page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('testimonial');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Testimonial').should('exist');
    cy.url().should('match', testimonialPageUrlPattern);
  });

  describe('Testimonial page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(testimonialPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Testimonial page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/testimonial/new$'));
        cy.getEntityCreateUpdateHeading('Testimonial');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testimonialPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/testimonials',
          body: testimonialSample,
        }).then(({ body }) => {
          testimonial = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/testimonials+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [testimonial],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(testimonialPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Testimonial page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('testimonial');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testimonialPageUrlPattern);
      });

      it('edit button click should load edit Testimonial page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Testimonial');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testimonialPageUrlPattern);
      });

      it('edit button click should load edit Testimonial page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Testimonial');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testimonialPageUrlPattern);
      });

      it('last delete button click should delete instance of Testimonial', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('testimonial').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', testimonialPageUrlPattern);

        testimonial = undefined;
      });
    });
  });

  describe('new Testimonial page', () => {
    beforeEach(() => {
      cy.visit(`${testimonialPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Testimonial');
    });

    it('should create an instance of Testimonial', () => {
      cy.get(`[data-cy="name"]`).type('and less');
      cy.get(`[data-cy="name"]`).should('have.value', 'and less');

      cy.get(`[data-cy="feedback"]`).type('../fake-data/blob/hipster.txt');
      cy.get(`[data-cy="feedback"]`).invoke('val').should('match', new RegExp('../fake-data/blob/hipster.txt'));

      cy.get(`[data-cy="rating"]`).type('2');
      cy.get(`[data-cy="rating"]`).should('have.value', '2');

      cy.get(`[data-cy="date"]`).type('2024-09-14T11:03');
      cy.get(`[data-cy="date"]`).blur();
      cy.get(`[data-cy="date"]`).should('have.value', '2024-09-14T11:03');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        testimonial = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', testimonialPageUrlPattern);
    });
  });
});
