/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import PostalCodeUpdate from './postal-code-update.vue';
import PostalCodeService from './postal-code.service';
import AlertService from '@/shared/alert/alert.service';

import SubDistrictService from '@/entities/sub-district/sub-district.service';

type PostalCodeUpdateComponentType = InstanceType<typeof PostalCodeUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const postalCodeSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<PostalCodeUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('PostalCode Management Update Component', () => {
    let comp: PostalCodeUpdateComponentType;
    let postalCodeServiceStub: SinonStubbedInstance<PostalCodeService>;

    beforeEach(() => {
      route = {};
      postalCodeServiceStub = sinon.createStubInstance<PostalCodeService>(PostalCodeService);
      postalCodeServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          'font-awesome-icon': true,
          'b-input-group': true,
          'b-input-group-prepend': true,
          'b-form-datepicker': true,
          'b-form-input': true,
        },
        provide: {
          alertService,
          postalCodeService: () => postalCodeServiceStub,
          subDistrictService: () =>
            sinon.createStubInstance<SubDistrictService>(SubDistrictService, {
              retrieve: sinon.stub().resolves({}),
            } as any),
        },
      };
    });

    afterEach(() => {
      vitest.resetAllMocks();
    });

    describe('save', () => {
      it('Should call update service on save for existing entity', async () => {
        // GIVEN
        const wrapper = shallowMount(PostalCodeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.postalCode = postalCodeSample;
        postalCodeServiceStub.update.resolves(postalCodeSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(postalCodeServiceStub.update.calledWith(postalCodeSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        postalCodeServiceStub.create.resolves(entity);
        const wrapper = shallowMount(PostalCodeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.postalCode = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(postalCodeServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        postalCodeServiceStub.find.resolves(postalCodeSample);
        postalCodeServiceStub.retrieve.resolves([postalCodeSample]);

        // WHEN
        route = {
          params: {
            postalCodeId: '' + postalCodeSample.id,
          },
        };
        const wrapper = shallowMount(PostalCodeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.postalCode).toMatchObject(postalCodeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        postalCodeServiceStub.find.resolves(postalCodeSample);
        const wrapper = shallowMount(PostalCodeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
