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

describe('MstProvince e2e test', () => {
  const mstProvincePageUrl = '/mst-province';
  const mstProvincePageUrlPattern = new RegExp('/mst-province(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const mstProvinceSample = { name: 'gah instead' };

  let mstProvince;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/mst-provinces+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/mst-provinces').as('postEntityRequest');
    cy.intercept('DELETE', '/api/mst-provinces/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (mstProvince) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/mst-provinces/${mstProvince.id}`,
      }).then(() => {
        mstProvince = undefined;
      });
    }
  });

  it('MstProvinces menu should load MstProvinces page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('mst-province');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('MstProvince').should('exist');
    cy.url().should('match', mstProvincePageUrlPattern);
  });

  describe('MstProvince page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(mstProvincePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create MstProvince page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/mst-province/new$'));
        cy.getEntityCreateUpdateHeading('MstProvince');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProvincePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/mst-provinces',
          body: mstProvinceSample,
        }).then(({ body }) => {
          mstProvince = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/mst-provinces+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              headers: {
                link: '<http://localhost/api/mst-provinces?page=0&size=20>; rel="last",<http://localhost/api/mst-provinces?page=0&size=20>; rel="first"',
              },
              body: [mstProvince],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(mstProvincePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details MstProvince page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('mstProvince');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProvincePageUrlPattern);
      });

      it('edit button click should load edit MstProvince page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstProvince');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProvincePageUrlPattern);
      });

      it.skip('edit button click should load edit MstProvince page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('MstProvince');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProvincePageUrlPattern);
      });

      it('last delete button click should delete instance of MstProvince', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('mstProvince').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', mstProvincePageUrlPattern);

        mstProvince = undefined;
      });
    });
  });

  describe('new MstProvince page', () => {
    beforeEach(() => {
      cy.visit(`${mstProvincePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('MstProvince');
    });

    it('should create an instance of MstProvince', () => {
      cy.get(`[data-cy="name"]`).type('run');
      cy.get(`[data-cy="name"]`).should('have.value', 'run');

      cy.get(`[data-cy="unm49Code"]`).type('joyously near furiously');
      cy.get(`[data-cy="unm49Code"]`).should('have.value', 'joyously near furiously');

      cy.get(`[data-cy="isoAlpha2Code"]`).type('tense sane far');
      cy.get(`[data-cy="isoAlpha2Code"]`).should('have.value', 'tense sane far');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        mstProvince = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', mstProvincePageUrlPattern);
    });
  });
});
