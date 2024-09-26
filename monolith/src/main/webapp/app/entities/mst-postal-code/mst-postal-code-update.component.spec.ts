/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';
import { type RouteLocation } from 'vue-router';

import MstPostalCodeUpdate from './mst-postal-code-update.vue';
import MstPostalCodeService from './mst-postal-code.service';
import AlertService from '@/shared/alert/alert.service';

import MstSubDistrictService from '@/entities/mst-sub-district/mst-sub-district.service';

type MstPostalCodeUpdateComponentType = InstanceType<typeof MstPostalCodeUpdate>;

let route: Partial<RouteLocation>;
const routerGoMock = vitest.fn();

vitest.mock('vue-router', () => ({
  useRoute: () => route,
  useRouter: () => ({ go: routerGoMock }),
}));

const mstPostalCodeSample = { id: 123 };

describe('Component Tests', () => {
  let mountOptions: MountingOptions<MstPostalCodeUpdateComponentType>['global'];
  let alertService: AlertService;

  describe('MstPostalCode Management Update Component', () => {
    let comp: MstPostalCodeUpdateComponentType;
    let mstPostalCodeServiceStub: SinonStubbedInstance<MstPostalCodeService>;

    beforeEach(() => {
      route = {};
      mstPostalCodeServiceStub = sinon.createStubInstance<MstPostalCodeService>(MstPostalCodeService);
      mstPostalCodeServiceStub.retrieve.onFirstCall().resolves(Promise.resolve([]));

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
          mstPostalCodeService: () => mstPostalCodeServiceStub,
          mstSubDistrictService: () =>
            sinon.createStubInstance<MstSubDistrictService>(MstSubDistrictService, {
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
        const wrapper = shallowMount(MstPostalCodeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstPostalCode = mstPostalCodeSample;
        mstPostalCodeServiceStub.update.resolves(mstPostalCodeSample);

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstPostalCodeServiceStub.update.calledWith(mstPostalCodeSample)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });

      it('Should call create service on save for new entity', async () => {
        // GIVEN
        const entity = {};
        mstPostalCodeServiceStub.create.resolves(entity);
        const wrapper = shallowMount(MstPostalCodeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        comp.mstPostalCode = entity;

        // WHEN
        comp.save();
        await comp.$nextTick();

        // THEN
        expect(mstPostalCodeServiceStub.create.calledWith(entity)).toBeTruthy();
        expect(comp.isSaving).toEqual(false);
      });
    });

    describe('Before route enter', () => {
      it('Should retrieve data', async () => {
        // GIVEN
        mstPostalCodeServiceStub.find.resolves(mstPostalCodeSample);
        mstPostalCodeServiceStub.retrieve.resolves([mstPostalCodeSample]);

        // WHEN
        route = {
          params: {
            mstPostalCodeId: '' + mstPostalCodeSample.id,
          },
        };
        const wrapper = shallowMount(MstPostalCodeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(comp.mstPostalCode).toMatchObject(mstPostalCodeSample);
      });
    });

    describe('Previous state', () => {
      it('Should go previous state', async () => {
        mstPostalCodeServiceStub.find.resolves(mstPostalCodeSample);
        const wrapper = shallowMount(MstPostalCodeUpdate, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();

        comp.previousState();
        await comp.$nextTick();

        expect(routerGoMock).toHaveBeenCalledWith(-1);
      });
    });
  });
});
