<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxTestimonial.home.createOrEditLabel"
          data-cy="TrxTestimonialCreateUpdateHeading"
          v-text="t$('monolithApp.trxTestimonial.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxTestimonial.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxTestimonial.id" readonly />
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTestimonial.name')" for="trx-testimonial-name"></label>
            <input
              type="text"
              class="form-control"
              name="name"
              id="trx-testimonial-name"
              data-cy="name"
              :class="{ valid: !v$.name.$invalid, invalid: v$.name.$invalid }"
              v-model="v$.name.$model"
              required
            />
            <div v-if="v$.name.$anyDirty && v$.name.$invalid">
              <small class="form-text text-danger" v-for="error of v$.name.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTestimonial.feedback')" for="trx-testimonial-feedback"></label>
            <textarea
              class="form-control"
              name="feedback"
              id="trx-testimonial-feedback"
              data-cy="feedback"
              :class="{ valid: !v$.feedback.$invalid, invalid: v$.feedback.$invalid }"
              v-model="v$.feedback.$model"
              required
            ></textarea>
            <div v-if="v$.feedback.$anyDirty && v$.feedback.$invalid">
              <small class="form-text text-danger" v-for="error of v$.feedback.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTestimonial.rating')" for="trx-testimonial-rating"></label>
            <input
              type="number"
              class="form-control"
              name="rating"
              id="trx-testimonial-rating"
              data-cy="rating"
              :class="{ valid: !v$.rating.$invalid, invalid: v$.rating.$invalid }"
              v-model.number="v$.rating.$model"
              required
            />
            <div v-if="v$.rating.$anyDirty && v$.rating.$invalid">
              <small class="form-text text-danger" v-for="error of v$.rating.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
          <div class="form-group">
            <label class="form-control-label" v-text="t$('monolithApp.trxTestimonial.date')" for="trx-testimonial-date"></label>
            <div class="d-flex">
              <input
                id="trx-testimonial-date"
                data-cy="date"
                type="datetime-local"
                class="form-control"
                name="date"
                :class="{ valid: !v$.date.$invalid, invalid: v$.date.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.date.$model)"
                @change="updateInstantField('date', $event)"
              />
            </div>
            <div v-if="v$.date.$anyDirty && v$.date.$invalid">
              <small class="form-text text-danger" v-for="error of v$.date.$errors" :key="error.$uid">{{ error.$message }}</small>
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
<script lang="ts" src="./trx-testimonial-update.component.ts"></script>
