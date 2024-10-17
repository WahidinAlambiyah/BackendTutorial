<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxDiscount.home.createOrEditLabel"
          data-cy="TrxDiscountCreateUpdateHeading"
          v-text="t$('monolithApp.trxDiscount.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxDiscount.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxDiscount.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxDiscount.discountPercentage')"
              for="trx-discount-discountPercentage"
            ></label>
            <input
              type="number"
              class="form-control"
              name="discountPercentage"
              id="trx-discount-discountPercentage"
              data-cy="discountPercentage"
              :class="{ valid: !v$.discountPercentage.$invalid, invalid: v$.discountPercentage.$invalid }"
              v-model.number="v$.discountPercentage.$model"
              required
            />
            <div v-if="v$.discountPercentage.$anyDirty && v$.discountPercentage.$invalid">
              <small class="form-text text-danger" v-for="error of v$.discountPercentage.$errors" :key="error.$uid">{{
                error.$message
              }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxDiscount.startDate')" for="trx-discount-startDate"></label>
            <div class="d-flex">
              <input
                id="trx-discount-startDate"
                data-cy="startDate"
                type="datetime-local"
                class="form-control"
                name="startDate"
                :class="{ valid: !v$.startDate.$invalid, invalid: v$.startDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.startDate.$model)"
                @change="updateInstantField('startDate', $event)"
              />
            </div>
            <div v-if="v$.startDate.$anyDirty && v$.startDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.startDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxDiscount.endDate')" for="trx-discount-endDate"></label>
            <div class="d-flex">
              <input
                id="trx-discount-endDate"
                data-cy="endDate"
                type="datetime-local"
                class="form-control"
                name="endDate"
                :class="{ valid: !v$.endDate.$invalid, invalid: v$.endDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.endDate.$model)"
                @change="updateInstantField('endDate', $event)"
              />
            </div>
            <div v-if="v$.endDate.$anyDirty && v$.endDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.endDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
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
<script lang="ts" src="./trx-discount-update.component.ts"></script>
