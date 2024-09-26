/* tslint:disable max-line-length */
import { vitest } from 'vitest';
import { shallowMount, type MountingOptions } from '@vue/test-utils';
import sinon, { type SinonStubbedInstance } from 'sinon';

import MstTask from './mst-task.vue';
import MstTaskService from './mst-task.service';
import AlertService from '@/shared/alert/alert.service';

type MstTaskComponentType = InstanceType<typeof MstTask>;

const bModalStub = {
  render: () => {},
  methods: {
    hide: () => {},
    show: () => {},
  },
};

describe('Component Tests', () => {
  let alertService: AlertService;

  describe('MstTask Management Component', () => {
    let mstTaskServiceStub: SinonStubbedInstance<MstTaskService>;
    let mountOptions: MountingOptions<MstTaskComponentType>['global'];

    beforeEach(() => {
      mstTaskServiceStub = sinon.createStubInstance<MstTaskService>(MstTaskService);
      mstTaskServiceStub.retrieve.resolves({ headers: {} });

      alertService = new AlertService({
        i18n: { t: vitest.fn() } as any,
        bvToast: {
          toast: vitest.fn(),
        } as any,
      });

      mountOptions = {
        stubs: {
          bModal: bModalStub as any,
          'font-awesome-icon': true,
          'b-badge': true,
          'b-button': true,
          'router-link': true,
        },
        directives: {
          'b-modal': {},
        },
        provide: {
          alertService,
          mstTaskService: () => mstTaskServiceStub,
        },
      };
    });

    describe('Mount', () => {
      it('Should call load all on init', async () => {
        // GIVEN
        mstTaskServiceStub.retrieve.resolves({ headers: {}, data: [{ id: 123 }] });

        // WHEN
        const wrapper = shallowMount(MstTask, { global: mountOptions });
        const comp = wrapper.vm;
        await comp.$nextTick();

        // THEN
        expect(mstTaskServiceStub.retrieve.calledOnce).toBeTruthy();
        expect(comp.mstTasks[0]).toEqual(expect.objectContaining({ id: 123 }));
      });
    });
    describe('Handles', () => {
      let comp: MstTaskComponentType;

      beforeEach(async () => {
        const wrapper = shallowMount(MstTask, { global: mountOptions });
        comp = wrapper.vm;
        await comp.$nextTick();
        mstTaskServiceStub.retrieve.reset();
        mstTaskServiceStub.retrieve.resolves({ headers: {}, data: [] });
      });

      it('Should call delete service on confirmDelete', async () => {
        // GIVEN
        mstTaskServiceStub.delete.resolves({});

        // WHEN
        comp.prepareRemove({ id: 123 });

        comp.removeMstTask();
        await comp.$nextTick(); // clear components

        // THEN
        expect(mstTaskServiceStub.delete.called).toBeTruthy();

        // THEN
        await comp.$nextTick(); // handle component clear watch
        expect(mstTaskServiceStub.retrieve.callCount).toEqual(1);
      });
    });
  });
});
