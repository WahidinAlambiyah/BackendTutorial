<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxCoupon.home.createOrEditLabel"
          data-cy="TrxCouponCreateUpdateHeading"
          v-text="t$('monolithApp.trxCoupon.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxCoupon.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxCoupon.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxCoupon.code')" for="trx-coupon-code"></label>
            <input
              type="text"
              class="form-control"
              name="code"
              id="trx-coupon-code"
              data-cy="code"
              :class="{ valid: !v$.code.$invalid, invalid: v$.code.$invalid }"
              v-model="v$.code.$model"
              required
            />
            <div v-if="v$.code.$anyDirty && v$.code.$invalid">
              <small class="form-text text-danger" v-for="error of v$.code.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxCoupon.discountAmount')" for="trx-coupon-discountAmount"></label>
            <input
              type="number"
              class="form-control"
              name="discountAmount"
              id="trx-coupon-discountAmount"
              data-cy="discountAmount"
              :class="{ valid: !v$.discountAmount.$invalid, invalid: v$.discountAmount.$invalid }"
              v-model.number="v$.discountAmount.$model"
              required
            />
            <div v-if="v$.discountAmount.$anyDirty && v$.discountAmount.$invalid">
              <small class="form-text text-danger" v-for="error of v$.discountAmount.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxCoupon.validUntil')" for="trx-coupon-validUntil"></label>
            <div class="d-flex">
              <input
                id="trx-coupon-validUntil"
                data-cy="validUntil"
                type="datetime-local"
                class="form-control"
                name="validUntil"
                :class="{ valid: !v$.validUntil.$invalid, invalid: v$.validUntil.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.validUntil.$model)"
                @change="updateInstantField('validUntil', $event)"
              />
            </div>
            <div v-if="v$.validUntil.$anyDirty && v$.validUntil.$invalid">
              <small class="form-text text-danger" v-for="error of v$.validUntil.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxCoupon.minPurchase')" for="trx-coupon-minPurchase"></label>
            <input
              type="number"
              class="form-control"
              name="minPurchase"
              id="trx-coupon-minPurchase"
              data-cy="minPurchase"
              :class="{ valid: !v$.minPurchase.$invalid, invalid: v$.minPurchase.$invalid }"
              v-model.number="v$.minPurchase.$model"
            />
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./trx-coupon-update.component.ts"></script>
