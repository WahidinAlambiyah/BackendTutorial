/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstCountryUpdate from './mst-country-update.vue';
import MstCountryService from './mst-country.service';
import AlertService from '@/shared/alert/alert.service';

import MstRegionService from '@/entities/mst-region/mst-region.service';

type MstCountryUpdateComponentType = InstanceType<typeof MstCountryUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstCountrySample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstCountryUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstCountry Management Update Component', () => {
    let comp: MstCountryUpdateComponentType;
    let mstCountryServiceStub: SinonStubbedInstance<MstCountryService>;

    beforeEach(() => {
      route = {};
      mstCountryServiceStub = sinon.createStubInstance<MstCountryService>(MstCountryService);
      mstCountryServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstCountryService: () => mstCountryServiceStub,
          mstRegionService: () =>
            sinon.createStubInstance<MstRegionService>(MstRegionService, {
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
        const wrapper = shallowMount(MstCountryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstCountry = mstCountrySample;
        mstCountryServiceStub.update.resolves(mstCountrySample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstCountryServiceStub.update.calledWith(mstCountrySample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstCountryServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstCountryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstCountry = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstCountryServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstCountryServiceStub.find.resolves(mstCountrySample);
        mstCountryServiceStub.retrieve.resolves([mstCountrySample]);

        // WHEN
        route = {
          params: {
            mstCountryId: '' + mstCountrySample.id,
          },
        };
        const wrapper = shallowMount(MstCountryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstCountry).toMatchObject(mstCountrySample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstCountryServiceStub.find.resolves(mstCountrySample);
        const wrapper = shallowMount(MstCountryUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
